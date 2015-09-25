package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.Pair;
import com.norswap.util.Two;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Resolves all resolvable references underneath the visited expression graph. Resolvable references
 * are those for which a target with the specified name exist within the expression graph.
 * <p>
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph.
 * <p>
 * If there are unresolved references, an exception is thrown (but if caught, the above still
 * applies).
 */
public class ReferenceResolver extends GraphVisitor<ParsingExpression>
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
    public Map<String, Slot<ParsingExpression>> named = new HashMap<>();

    // ---------------------------------------------------------------------------------------------

    /**
     * All the regular TODO
     */
    public Set<Slot<ParsingExpression>> references = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ReferenceResolver()
    {
        super(Walks.copyOnWriteWalk());
    }

    // ---------------------------------------------------------------------------------------------

    public ReferenceResolver(GraphWalker<ParsingExpression> walker)
    {
        super(walker);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void afterChild(ParsingExpression pe, Slot<ParsingExpression> slot, NodeState state)
    {
        ParsingExpression child = slot.get();

        if (child.name != null)
        {
            named.put(child.name, slot);
        }

        if (child instanceof Reference)
        {
            references.add(slot);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterRoot(Slot<ParsingExpression> slot, NodeState state)
    {
        afterChild(null, slot, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void conclude()
    {
        HashSet<String> unresolved = new HashSet<>();
        Array<Two<Slot<ParsingExpression>>> pairs = new Array<>(references.size());

        for (Slot<ParsingExpression> slot: references)
        {
            Slot<ParsingExpression> target = slot;
            String name;
            int i = 0;

            // References can be chained!

            do {
                name = ((Reference) target.get()).target;
                target = named.get(name);

                if (++i > REFERENCE_CHAIN_LIMIT)
                {
                    panic();
                }
            }
            while (target != null && target.get() instanceof Reference);

            if (slot == null)
            {
                unresolved.add(name);
            }
            else
            {
                ParsingExpression pe = target.get();
                slot.set(pe);
                pe.name = name;
                pairs.add(new Two<>(slot, target));
            }
        }

        if (!unresolved.isEmpty())
        {
            throw new RuntimeException(
                "There were unresolved references in the grammar: " + unresolved);
        }

        for (Two<Slot<ParsingExpression>> pair: pairs)
        {
            pair.a.set(pair.b.get());
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void panic()
    {
        throw new RuntimeException(
            "It is likely that you have a rule which is a reference to itself. "
            + "If it is not the case and you use more than " + REFERENCE_CHAIN_LIMIT
            + " chained references, increase the value of "
            + "ReferenceResolver.REFERENCE_CHAIN_LIMIT.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
