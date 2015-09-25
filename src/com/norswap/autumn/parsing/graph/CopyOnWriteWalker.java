package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Filter;
import com.norswap.util.Array;
import com.norswap.util.MultiMap;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.slot.Slot;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.stream;

/**
 * Walks the whole graphs, modifications causes all ancestors of the modified nodes to be copied.
 * <p>
 * TODO
 * <p>
 * An alternative is to make a full copy of the parsing expression graph with a {@link Copier} then
 * to run your modification in-place on the copy. Unlike this walker, {@link Copier} guarantees
 * there won't be any shared parsing expression between the original graph and the copy.
 */
public final class CopyOnWriteWalker implements GraphWalker<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    MultiMap<ParsingExpression, Slot<ParsingExpression>> slotsFor = new MultiMap<>();

    HashMap<ParsingExpression, ParsingExpression> copyMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<Slot<ParsingExpression>>
    children(Slot<ParsingExpression> slot, GraphVisitor<ParsingExpression> visitor)
    {
        ParsingExpression pe = slot.get();

        if (slotsFor.get(pe).isEmpty())
        {
            slotsFor.add(pe, slot);
        }

        return Walks.helper(
            stream(pe.children()),
            (c, x) ->
            {
                CopyChildSlot child = new CopyChildSlot(this, pe, c.i++);
                slotsFor.add(x, child);
                return child;
            });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
