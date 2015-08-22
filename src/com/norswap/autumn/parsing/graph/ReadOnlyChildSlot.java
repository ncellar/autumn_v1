package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.slot.Slot;

/**
 * A read-only child slot ({@link #set} is a no-op).
 */
public final class ReadOnlyChildSlot extends ChildSlot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ReadOnlyChildSlot(ParsingExpression pe, int index)
    {
        super(pe, index);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Slot<ParsingExpression> set(ParsingExpression child)
    {
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
