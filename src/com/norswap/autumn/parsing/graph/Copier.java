package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.Counter;
import com.norswap.util.JArrays;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs a complete deep copy of a parsing expression.
 * <p>
 * In case you want to perform a transformation on the copy, an alternative is to use a {@link
 * CopyOnWriteWalker}. Contrary to a visitor using the copy on write walker, this visitor guarantees
 * there won't be any shared parsing expression between the original graph and the copy.
 */
public final class Copier extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final static class CopyWalker implements GraphWalker<ParsingExpression>
    {
        @Override
        public List<Slot<ParsingExpression>>
        children(ParsingExpression pe, GraphVisitor<ParsingExpression> visitor)
        {
            Copier copier = (Copier) visitor;
            ParsingExpression copy = copier.copyStack.peek();
            Counter c = new Counter();
            Object[] slots = JArrays.map(pe.children(), x -> new ChildSlot(copy, c.i++));
            return Array.<Slot<ParsingExpression>>fromUnsafe(slots);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Array<ParsingExpression> copyStack = new Array<>();
    Map<ParsingExpression, ParsingExpression> copies = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Copier()
    {
        super(new CopyWalker());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(ParsingExpression pe)
    {
        ParsingExpression copy = pe.clone();
        copy.copyOwnData();

        copyStack.push(copy);
        copies.put(pe, copy);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(ParsingExpression parent, Slot<ParsingExpression> slot, NodeState state)
    {
        switch (state)
        {
            case CUTOFF:
            case VISITED:
                slot.set(copies.get(slot.get()));
                break;

            default:
                slot.set(copyStack.pop());
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterRoot(Slot<ParsingExpression> slot, NodeState state)
    {
        afterChild(null, slot, state);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
