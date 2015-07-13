package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.IncrementalReferenceResolver;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.slot.Slot;
import com.norswap.util.MultiMap;

import java.util.HashMap;

/**
 * Resolves all resolvable references within a number of (possibly mutually referencing) parsing
 * expressions. Resolvable references are those for which a target with the specified name exist
 * within the expression graph.
 *
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph.
 *
 * This is the preferred way to resolve references in an expression that was constructed
 * automatically. If you use factory methods, the method {@link ParsingExpressionFactory#recursive$}
 * which uses a {@link IncrementalReferenceResolver} is an alternative.
 *
 * Implementation-wise, this is an expression graph transformer. The walk records named
 * expressions as they are encountered. The transformation replaces a reference with its target, if
 * it has already been encountered. Otherwise the reference stays, but the its
 * location (a {@link ChildSlot}) is recorded as needing to be assigned whenever the target is
 * encountered.
 *
 * A reference's target might be another reference. We handle these cases by recursively
 * resolving until we encounter a non-reference target, or a missing target.
 */
public final class ReferenceResolver extends ExpressionGraphTransformer
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Max allowed number of chained references. Used to detect reference loops (not be confused
     * with loops in the grammar -- reference loops involve only references and no actual
     * parsing expressions).
     */
    public static int REFERENCE_CHAIN_LIMIT = 10000;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Maps names (e.g. rule names) to the expression they designate.
     */
    private HashMap<String, ParsingExpression> named;

    /**
     * Map target names that can't be resolved to a slot.
     */
    private MultiMap<String, Slot<ParsingExpression>> unresolved;

    /**
     * If {@link #transform} encounters a missing target, it will record its name here for the
     * benefit of calling functions, who can associate a location to it.
     */
    private String unresolvedTarget;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression resolve(ParsingExpression expr)
    {
        return new ReferenceResolver().walk(expr);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setup()
    {
        super.setup();
        named = new HashMap<>();
        unresolved = new MultiMap<>();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void teardown()
    {
        try
        {
            if (!unresolved.isEmpty())
            {
                throw new RuntimeException(
                    "There were unresolved references in the grammar: " + unresolved.keySet());
            }
        }
        finally
        {
            super.teardown();
            named = null;
            unresolved = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression doTransform(ParsingExpression pe)
    {
        int i = 0;
        while (pe instanceof Reference)
        {
            String targetName = ((Reference) pe).target;
            ParsingExpression target = named.get(targetName);

            if (++i > REFERENCE_CHAIN_LIMIT)
            {
                throw new RuntimeException(
                    "It is likely that you have a rule which is a reference to itself. "
                    + "If it is not the case and you use more than " + REFERENCE_CHAIN_LIMIT
                    + " chained references, increase the value of "
                    + "ReferenceResolver.REFERENCE_CHAIN_LIMIT.");
            }

            if (target == null)
            {
                unresolvedTarget = targetName;
                break;
            }

            pe = target;
        }

        return pe;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void before(ParsingExpression pe)
    {
        String name = pe.name();

        if (name != null)
        {
            named.put(name, pe);
            attemptResolvingReferencesTo(name);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void afterChild(ParsingExpression pe, Slot<ParsingExpression> slot, State state)
    {
        // Apply the transform.
        super.afterChild(pe, slot, state);

        saveUnresolvedReference(slot);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void afterRoot(Slot<ParsingExpression> slot)
    {
        // Apply the transform.
        super.afterRoot(slot);

        saveUnresolvedReference(slot);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void attemptResolvingReferencesTo(String name)
    {
        for (Slot<ParsingExpression> slot: unresolved.remove(name))
        {
            slot.set(transform(slot.get()));
            saveUnresolvedReference(slot);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void saveUnresolvedReference(Slot<ParsingExpression> slot)
    {
        if (unresolvedTarget != null)
        {
            unresolved.add(unresolvedTarget, slot);
            unresolvedTarget = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
