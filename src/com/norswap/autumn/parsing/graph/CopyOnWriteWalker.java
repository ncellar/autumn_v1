package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.slot.Slot;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.stream;

/**
 * Walks the whole graphs, modifications causes all ancestors of the modified nodes to be copied.
 * <p>
 * For this to work, it is necessary for the visitor to call {@link #propagateChange(Slot)} from the
 * {@link GraphVisitor#afterChild} and {@link GraphVisitor#afterRoot} if the slot has not been
 * assigned otherwise.
 * <p>
 * An alternative is to make a full copy of the parsing expression graph with a {@link Copier} then
 * to run your modification in-place on the copy. Unlike this walker, {@link Copier} guarantees
 * there won't be any shared parsing expression between the original graph and the copy.
 */
public final class CopyOnWriteWalker implements GraphWalker<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParsingExpression, ParsingExpression> copyMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<Slot<ParsingExpression>>
    children(ParsingExpression pe, GraphVisitor<ParsingExpression> visitor)
    {
        return Walks.helper(
            stream(pe.children()),
            c -> new CopyChildSlot(copyMap, pe, c.i++));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void propagateChange(Slot<ParsingExpression> slot)
    {
        ParsingExpression copy = copyMap.get(slot.get());

        if (copy != null)
        {
            slot.set(copy);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
