package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.slot.Slot;

import java.util.Map;

/**
 * A child slot whose {@link #set} method retrieves or creates a copy of the parent in a map from
 * original to copies, then sets the child on this copy.
 * <p>
 * Use by {@link Walks#copyOnWriteWalk}.
 */
public final class CopyChildSlot extends ChildSlot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    final Map<ParsingExpression, ParsingExpression> copyMap;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public CopyChildSlot(Map<ParsingExpression, ParsingExpression> copyMap, ParsingExpression pe, int index)
    {
        super(pe, index);
        this.copyMap = copyMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Slot<ParsingExpression> set(ParsingExpression child)
    {
        ParsingExpression parent = copyMap.computeIfAbsent(pe, pe -> pe.clone());
        parent.setChild(index, child);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
