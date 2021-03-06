package com.norswap.util;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

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

    @SafeVarargs
    public static <T> T[] concat(IntFunction<T[]> supplier, T[]... arrays)
    {
        int size = 0;
        for (T[] array : arrays) {
            size += array.length;
        }

        T[] out = supplier.apply(size);

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
     * Concatenate all arrays into a single newly-allocated one, and returns it.
     * This is a method that is suspiciously absent of Guava, although the primitive pendants
     * do exist.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[]... arrays)
    {
        Class<?> type = arrays[0].getClass().getComponentType();

        return concat(
            size -> size == 0
                ? (T[]) new Object[0]
                : (T[]) java.lang.reflect.Array.newInstance(type, size),
            arrays);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copies bytes src[i2] to src[i2 + size - 1] into dst[i1] to dst[i1 + size - 1].
     * Both arrays must be sufficiently large for this to be possible.
     * Returns dst.
     */
    public static <T> T[] setRange(T[] dst, int i1, T[] src, int i2, int size)
    {
        assert dst.length >= i1 + size;
        assert src.length >= i2 + size;

        System.arraycopy(src, i2, dst, i1, size);

        return dst;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all elements of {@code src}, placing the results in the
     * corresponding slot of {@code dst}, which is then returned.
     */
    public static <T, U> U[]
    map(T[] src, U[] dst, Function<? super T, ? extends U> f)
    {
        assert dst.length >= src.length;

        for (int i = 0; i < src.length; ++i)
        {
            dst[i] = f.apply(src[i]);
        }

        return dst;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all elements of {@code src}, placing the results in the
     * corresponding slot of a new array, which is then returned. Because of type erasure, the
     * returned array has type {@code Object[]}.
     */
    public static <T> Object[]
    map(T[] src, Function<? super T, Object> f)
    {
        return map(src, new Object[src.length], f);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all elements of {@code src}, placing the results in the
     * corresponding slot of a new array created using {@code arrayGen}, which is then returned.
     */
    public static <T, U> U[]
    map(T[] src, IntFunction<U[]> arrayGen, Function<? super T, ? extends U> f)
    {
        return map(src, arrayGen.apply(src.length), f);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all pairs of elements (src1[i], src2[i]) for all {@code 0 <= i <
     * min(src1.length, src2.length)}, placing the results in the corresponding slot of {@code dst},
     * which is then returned.
     */
    public static <T, U, R> R[]
    bimap(T[] src1, U[] src2, R[] dst, BiFunction<? super T, ? super U, ? extends R> f)
    {
        int min = Math.min(src1.length, src2.length);

        assert dst.length >= min;

        for (int i = 0; i < min; ++i)
        {
            dst[i] = f.apply(src1[i], src2[i]);
        }

        return dst;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all pairs of elements (src1[i], src2[i]) for all {@code 0 <= i <
     * min(src1.length, src2.length)}, placing the results in the corresponding slot of a new array,
     * which is then returned. Because of type erasure, the returned array has type {@code
     * Object[]}.
     */
    public static <T, U> Object[]
    bimap(T[] src1, U[] src2, BiFunction<? super T, ? super U, ?> f)
    {
        return bimap(src1, src2, new Object[Math.min(src1.length, src2.length)], f);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the function f to all pairs of elements (src1[i], src2[i]) for all {@code 0 <= i <
     * min(src1.length, src2.length)}, placing the results in the corresponding slot of a new array
     * created using {@code arrayGen}, which is then returned.
     */
    public static <T, U, R> R[]
    bimap(T[] src1, U[] src2, IntFunction<R[]> arrayGen, BiFunction<? super T, ? super U, ? extends R> f)
    {
        return bimap(src1, src2, arrayGen.apply(Math.min(src1.length, src2.length)), f);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the first item of {@code array} to satisfy {@code predicate}, or null if none do.
     */
    public static <T> T
    first(T[] array, Predicate<? super T> predicate)
    {
        for (int i = 0; i < array.length; ++i)
        {
            T elem = array[i];

            if (predicate.test(elem))
                return elem;
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns an {@link Array} containing the items of {@code array} satisfying {@code predicate},
     * in the same order.
     */
    public static <T> Array<T>
    filter(T[] array, Predicate<? super T> predicate)
    {
        Array<T> out = new Array<>();

        for (int i = 0; i < array.length; ++i)
        {
            @SuppressWarnings("unchecked")
            T elem = (T) array[i];

            if (predicate.test(elem))
                out.add(elem);
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the index where {@code elem} appears first in {@code array}, or -1 if it doesn't
     * appear.
     */
    public static <T, U extends T> int indexOf(T[] array, U elem)
    {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(elem))
                return i;

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns true iif the {@code array} contains {@code elem}.
     */
    public static <T, U extends T> boolean contains(T[] array, U elem)
    {
        return indexOf(array, elem) != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public static <T> void forEach(T[] array, Consumer<T> consumer)
    {
        for (T t: array)
            consumer.accept(t);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
