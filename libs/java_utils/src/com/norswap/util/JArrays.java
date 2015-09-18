package com.norswap.util;


/**
 * Utilities to deal with plain Java arrays.
 */
public final class JArrays
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A simple syntactic sugar for array creation that will do type inference.
     * When passing 0 parameters, T will default to Object.
     */
    @SafeVarargs
    public static <T> T[] array(T... ts)
    {
        return ts;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the given array if its size is superior to the given size, otherwise returns
     * a new array of the same type with the given size.
     */
    public static <T> T[] largeEnough(T[] array, int size)
    {
        return array.length >= size ? array : newInstance(array, size);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a new array of the same type as the witness, with the given size.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newInstance(T[] witness, int size)
    {
        return (T[]) java.lang.reflect.Array.newInstance(witness.getClass().getComponentType(), size);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Concatenate all arrays into a single newly-allocated one, and returns it.
     * This is a method that is suspiciously absent of Guava, although the primitive pendants
     * do exist.
     */
    @SafeVarargs
    public static <T> T[] concat(T[]... arrays)
    {
        int size = 0;
        for (T[] array : arrays) {
            size += array.length;
        }

        T[] out = arrays.length == 0
            ? Caster.cast(new Object[0])
            : Caster.cast(java.lang.reflect.Array.newInstance(arrays[0].getClass().getComponentType(), size));

        int i = 0;
        for (T[] array : arrays)
        {
            setRange(out, i, array, 0, array.length);
            i += array.length;
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Concatenates all items in {@code ts} to the given array.
     */
    public static <T> T[] concat(T[] array, T... ts)
    {
        return concat(array, ts);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copies bytes src[i2] to src[i2 + size - 1] into dst[i1] to dst[i1 + size - 1].
     * Both arrays must be sufficiently large for this to be possible.
     * Returns dst.
     */
    public static <T> T[] setRange(T[] dst, int i1, T[] src, int i2, int size)
    {
        assert dst.length <= i1 + size;
        assert src.length <= i2 + size;

        for (int i = 0 ; i < size ; ++i) {
            dst[i1 + i] = src[i2 + i];
        }

        return dst;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
