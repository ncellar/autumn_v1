package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.slot.Slot;

/**
 * A slot corresponding to an indexed child of a parsing expression.
 */
public class ChildSlot implements Slot<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ChildSlot(ParsingExpression pe, int index)
    {
        this.pe = pe;
        this.index = index;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;
    public final int index;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression get()
    {
        return pe.children()[index];
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Slot<ParsingExpression> set(ParsingExpression child)
    {
        pe.setChild(index, child);
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "child [" + index + "] of " + pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
