package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.Caster;

import java.util.HashMap;

/**
 * Instantiate this class then run its {@link #walk} method to all resolvable references within
 * an expression. Resolvable references are those for which a target with the specified name
 * exist within the expression graph.
 *
 * It assumes that it is passed a tree (no loops). References are always leaves in this tree.
 *
 * As a result of the resolution process, all {@link Reference} nodes that have been resolved are
 * pruned from the expression tree and replaced with edge towards the expression they referenced,
 * hence making the tree a graph.
 *
 * This is the preferred way to resolve references in a expression that was constructed
 * automatically. If you use factory methods, the method {@link ParsingExpressionFactory#recursive$}
 * which uses a {@link SingleReferenceResolver} is an alternative.
 */
public final class AllReferenceResolver
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

    public void walk(ParsingExpression pe)
    {
        ParsingExpression[] children = pe.children();

        for (int i = 0; i < children.length; ++i)
        {
            if (children[i] instanceof Reference)
            {
                Reference ref = Caster.cast(children[i]);

                ParsingExpression target = named.get(ref.target);

                if (target != null)
                {
                    pe.setChild(i, target);
                }
                else
                {
                    Slot slot = new Slot();
                    slot.pe = pe;
                    slot.index = i;
                    unresolved.computeIfAbsent(ref.target, x -> new Array<>()).add(slot);
                }
            }
            else
            {
                String name = children[i].name();

                if (name != null)
                {
                    named.put(name, pe);

                    Array<Slot> slots = unresolved.remove(name);

                    if (slots != null)
                    for (Slot slot: slots)
                    {
                        slot.pe.setChild(slot.index, pe);
                    }
                }

                walk(children[i]);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
