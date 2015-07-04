package com.norswap.autumn.parsing.graph.slot;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * A slot corresponding to an indexed child of a parsing expression.
 */
public final class ChildSlot implements Slot<ParsingExpression>
{
    public final ParsingExpression pe;
    public final int index;

    public ChildSlot(ParsingExpression pe, int index)
    {
        this.pe = pe;
        this.index = index;
    }

    @Override
    public Slot<ParsingExpression> set(ParsingExpression child)
    {
        pe.setChild(index, child);
        return this;
    }

    @Override
    public ParsingExpression get()
    {
        return pe.children()[index];
    }
}
