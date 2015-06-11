package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.Caster;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Instantiate this class then run its {@link #resolve} method to resolve all resolvable
 * references within a number of (possibly mutually referencing) parsing expressions. Resolvable
 * references are those for which a target with the specified name exist within the expression
 * graph.
 *
 * {@link #resolve} modifies the expressions it is passed. It also modifies the array if its
 * items happen to be references. The method returns its parameter.
 *
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph.
 *
 * This is the preferred way to resolve references in a expression that was constructed
 * automatically. If you use factory methods, the method {@link ParsingExpressionFactory#recursive$}
 * which uses a {@link IncrementalReferenceResolver} is an alternative.
 */
public final class ReferenceResolver
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Designate a location where the target of a reference should be inserted.
     *
     * Used when a reference can't be resolved immediately (because its target hasn't been
     * encountered yet).
     */
    private static class Slot
    {
        ParsingExpression pe;
        int index;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Maps names (e.g. rule names) to the expression they designate.
     */
    private HashMap<String, ParsingExpression> named = new HashMap<>();

    /**
     * Map target names that can't be resolved to a slot.
     */
    private HashMap<String, Array<Slot>> unresolved = new HashMap<>();

    /**
     * Avoid loops.
     */
    private HashSet<ParsingExpression> visited = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression[] run(ParsingExpression[] exprs)
    {
        return new ReferenceResolver().resolve(exprs);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression run(ParsingExpression expr)
    {
        return new ReferenceResolver().resolve(expr);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression resolve(ParsingExpression expr)
    {
        return resolve(new ParsingExpression[]{expr})[0];
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression[] resolve(ParsingExpression[] exprs)
    {
        for (ParsingExpression pe: exprs)
        {
            walk(pe);
        }

        // Replace the references inside the array by their target.

        for (int i = 0; i < exprs.length; ++i)
        {
            ParsingExpression pe = exprs[i];

            // A reference's target can be another reference!
            while (pe instanceof Reference)
            {
                pe = named.get(((Reference) pe).target);
            }

            exprs[i] = pe;
        }

        return exprs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void walk(ParsingExpression pe)
    {
        if (visited.contains(pe))
        {
            return;
        }
        else
        {
            visited.add(pe);
        }

        String name = pe.name();

        if (name != null)
        {
            named.put(name, pe);
        }

        Array<Slot> slots = unresolved.remove(name);

        if (slots != null) {
            for (Slot slot: slots) {
                attemptFilingSlot(slot, pe, name);
            }
        }

        ParsingExpression[] children = pe.children();

        for (int i = 0; i < children.length; ++i)
        {
            if (children[i] instanceof Reference)
            {
                Reference ref = Caster.cast(children[i]);

                Slot slot = new Slot();
                slot.pe = pe;
                slot.index = i;

                attemptFilingSlot(slot, named.get(ref.target), ref.target);
            }
            else
            {
                walk(children[i]);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void attemptFilingSlot(Slot slot, ParsingExpression target, String targetName)
    {
        // A reference's target can be another reference!
        int j = 0;
        while (target instanceof Reference)
        {
            targetName = ((Reference) target).target;
            target = named.get(targetName);

            if (++j > 10000)
            {
                throw new RuntimeException(
                    "It is likely that you have a rule which is a reference to itself.");
            }
        }

        if (target != null)
        {
            slot.pe.setChild(slot.index, target);
        }
        else
        {
            unresolved.computeIfAbsent(targetName, x -> new Array<>()).add(slot);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
