package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.slot.Slot;

/**
 * A child slot whose {@link #set} method retrieves or creates a copy of the parent in a map from
 * original to copies, then sets the child on this copy.
 * <p>
 * Use by {@link Walks#copyOnWriteWalk}.
 */
public final class CopyChildSlot extends ChildSlot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final CopyOnWriteWalker walker;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public CopyChildSlot(CopyOnWriteWalker walker, ParsingExpression pe, int index)
    {
        super(pe, index);
        this.walker = walker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public ParsingExpression get()
    {
        ParsingExpression parent = walker.copyMap.get(pe);
        return parent == null ? pe.children()[index] : parent.children()[index];
    }

    @Override
    public Slot<ParsingExpression> set(ParsingExpression child)
    {
        ParsingExpression parent = walker.copyMap.get(pe);

        if (parent == null)
        {
            parent = pe.clone();
            walker.copyMap.put(pe, parent);

            for (Slot<ParsingExpression> parentSlot : walker.slotsFor.get(pe))
            {
                // TODO speedup with a private method
                parentSlot.set(parent);
            }
        }

        parent.setChild(index, child);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
