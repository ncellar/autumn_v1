package com.norswap.autumn.util;

/**
 * This interface indicates that the object is capable of making a deep copy of itself
 * (or in the case of containers, that it will perform a best effort towards this goal, falling
 * back on {@link #clone} or reference copy if necessary).
 *
 * The object for the copy should be obtained by calling {@link #clone} or {@code super.deepCopy()}
 * in case of a subclass. Ultimately the returned object *must* have been obtained by a call to
 * {@link #clone}.
 *
 * It is expected that objects that implement this interface will also implement {@link Cloneable}
 * properly.
 *
 * Subclasses that would like not to support this operation can throw {@link DeepCopy.NotSupported}.
 */
public interface DeepCopy extends Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class NotSupported extends RuntimeException {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    DeepCopy deepCopy();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
