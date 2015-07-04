package com.norswap.autumn.parsing.graph.slot;

import com.norswap.autumn.parsing.graph.slot.Slot;

/**
 * A immutable (non-settable) slot.
 */
public class ImmutableSlot<T> implements Slot<T>
{
    T t;

    public ImmutableSlot(T t)
    {
        this.t = t;
    }

    @Override
    public Slot<T> set(T t)
    {
        throw new UnsupportedOperationException("Trying to set an immutable slot.");
    }

    @Override
    public T get()
    {
        return t;
    }
}
