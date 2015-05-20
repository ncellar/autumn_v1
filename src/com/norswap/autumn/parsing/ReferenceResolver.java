package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.Caster;

import java.util.HashMap;

/**
 * Instantiate this class then run its {@link #resolve} method to resolve all resolvable
 * references within a number of (possibly mutually referencing) parsing expressions. Resolvable
 * references are those for which a target with the specified name exist within the expression
 * graph.
 *
 * It assumes that it is passed a tree (no loops). References are always leaves in this tree.
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

    private static class Slot
    {
        ParsingExpression pe;
        int index;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<String, ParsingExpression> named = new HashMap<>();

    private HashMap<String, Array<Slot>> unresolved = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] resolve(ParsingExpression[] exprs)
    {
        if (exprs == null || exprs.length == 0)
        {
            throw new RuntimeException("No expressions to resolve over.");
        }

        for (ParsingExpression pe: exprs)
        {
            walk(pe);
        }

        for (int i = 0; i < exprs.length; ++i)
        {
            ParsingExpression pe = exprs[i];

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
        String name = pe.name();

        if (name != null)
        {
            named.put(name, pe);
        }

        Array<Slot> slots = unresolved.remove(name);

        if (slots != null) for (Slot slot: slots)
        {
            attemptFilingSlot(slot, pe, name);
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

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression getFinalTarget(ParsingExpression target)
    {


        return target;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
