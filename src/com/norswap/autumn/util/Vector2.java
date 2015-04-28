package com.norswap.autumn.util;

import java.util.Arrays;

/**
 * A Vector2 is essentially a pair of resizable arrays that can be used to store pairs. It can be
 * used to implement a linear-time lookup map.
 *
 * It comes with the minimal set of feature required by the implementation.
 *
 * The initial size and the growth factor are controlled via static variables.
 */
public final class Vector2<T1, T2>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int INITIAL_SIZE = 4;

    public static float GROWTH_FACTOR = 2;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private T1[] t1s;
    private T2[] t2s;

    private int size;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Uses the supplied arrays as initial backing arrays.
     */
    public Vector2(T1[] t1s, T2[] t2s)
    {
        this.t1s = t1s;
        this.t2s = t2s;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does not guarantee the uniqueness of either t1 or t2.
     */
    @SuppressWarnings("unchecked")
    public void add(T1 t1, T2 t2)
    {
        if (size == t1s.length)
        {
            // `+ 1` handles the case where `tls.length == 0`

            t1s = Arrays.copyOf(t1s, (int) ((t1s.length + 1) * GROWTH_FACTOR));
            t2s = Arrays.copyOf(t2s, (int) ((t1s.length + 1) * GROWTH_FACTOR));
        }

        t1s[size] = t1;
        t2s[size] = t2;
        ++size;
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        assert(size <= this.size);

        if (t1s != null)
        {
            for (int i = size; i < this.size; ++i)
            {
                t1s[i] = null;
                t2s[i] = null;
            }

            this.size = size;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public T2 get(T1 t1)
    {
        for (int i = 0; i < size; ++i)
        {
            if (t1.equals(t1s[i]))
            {
                return t2s[i];
            }
        }

        return null;
    }

    public int indexOf1(T1 t1)
    {
        for (int i = 0; i < size; ++i)
        {
            if (t1.equals(t1s[i]))
            {
                return i;
            }
        }

        return -1;
    }

    public int indexOf2(T2 t2)
    {
        for (int i = 0; i < size; ++i)
        {
            if (t2.equals(t2s[i]))
            {
                return i;
            }
        }

        return -1;
    }

    public T1 get1(int index)
    {
        return t1s[index];
    }

    public T2 get2(int index)
    {
        return t2s[index];
    }

    public void set1(int index, T1 t1)
    {
        t1s[index] = t1;
    }

    public void set2(int index, T2 t2)
    {
        t2s[index] = t2;
    }

    public void set(int index, T1 t1, T2 t2)
    {
        t1s[index] = t1;
        t2s[index] = t2;
    }

    // ---------------------------------------------------------------------------------------------

    public void removeLast()
    {
        assert(size > 0);

        --size;
        t1s[size] = null;
        t2s[size] = null;
    }

    // ---------------------------------------------------------------------------------------------

    public int size()
    {
        return size;
    }

    public T1[] _1()
    {
        return t1s;
    }

    public T2[] _2()
    {
        return t2s;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
