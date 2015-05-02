package com.norswap.autumn.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A dynamic array to serve as minimal substitute to ArrayList.
 *
 * The idea to to be able to implement functions not implemented by ArrayList, such as {@link
 * #truncate}.
 */
public final class Array<T> implements Iterable<T>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int DEFAULT_SIZE = 4;
    public static double GROWTH_FACTOR = 2.0f;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Object[] array;
    private int next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Array(int size)
    {
        array = new Object[size];
    }

    public Array()
    {
        array = new Object[DEFAULT_SIZE];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int size()
    {
        return next;
    }

    // ---------------------------------------------------------------------------------------------

    public void add(T t)
    {
        if (next == array.length)
        {
            grow(array.length + 1);
        }

        array[next++] = t;
    }

    // ---------------------------------------------------------------------------------------------

    public T get(int index)
    {
        return Caster.cast(array[index]);
    }

    // ---------------------------------------------------------------------------------------------

    public void set(int index, T t)
    {
        array[index] = t;
    }

    // ---------------------------------------------------------------------------------------------

    public void grow(int capacity)
    {
        int size = array.length;

        while (size < capacity)
        {
            size = (int) (size * GROWTH_FACTOR);
        }

        array = Arrays.copyOf(array, size);
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        for (int i = size; i < next; ++i)
        {
            array[i] = null;
        }

        if (size < next)
        {
            next = size;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Collection<T> collection)
    {
        for (T t: collection)
        {
            add(t);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Array<T> array)
    {
        for (int i = 0; i < array.size(); ++i)
        {
            add(array.get(i));
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void push(T t)
    {
        add(t);
    }

    // ---------------------------------------------------------------------------------------------

    public T pop()
    {
        return Caster.cast(array[--next]);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private int index;

            @Override
            public boolean hasNext()
            {
                return index < next;
            }

            @Override
            public T next()
            {
                return Caster.cast(array[index++]);
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
