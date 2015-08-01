package com.norswap.util.lambda;

import java.util.Arrays;

public class Curry
{
    protected Object function;
    protected Object[] pi;
    protected int[] i;
    protected int[] pei;

    public Object apply(Object... pe)
    {
        switch (i.length)
        {
            case 0:
                return ((F0) function).apply();

            case 1:
                return ((F1) function).apply(
                    i[0] != -1 ? pi[i[0]] : pe[0]);

            case 2:
                return ((F2) function).apply(
                    i[0] >= 0 ? pi[i[0]] : pe[-i[0]-1],
                    i[1] >= 0 ? pi[i[1]] : pe[-i[0]-1]);

            default:
                return null; // TODO
        }
    }

    // TODO
    // this is not correct
    // because order of missing (negative) params in i[] might not be order of params in type
    // use new field pei to store (order param -> index in i[])
    // allow passing nulls inside array (and make static field _ = null) for holes
    // count nulls to determine by how much to grown new array

    public Curry curry(Object... pe)
    {
        Curry out = new Curry();
        out.function = function;
        out.pi = Arrays.copyOf(pi, pi.length + pe.length);
        out.i = i.clone();

        int k = pi.length;
        int l = 0;

        for (int j = 0; l < pe.length; ++j)
        {
            if (out.i[j] < 0)
            {
                out.i[j] = k;
                out.pi[k++] = pe[l++];
            }
        }

        return out;
    }

    public Curry curry2(Object... pe)
    {
        int len = pe.length >> 1;

        for (int i = 0; i < len; ++i)
        {
            int min = (int) pe[i];
            int mini = i;

            for (int j = i + 1; j < len; ++j)
            {
                int x = (int) pe[j];
                if (x < min)
                {
                    min = x;
                    mini = j;
                }
            }

            Object tmp = pe[i];
            pe[i] = min;
            pe[mini] = tmp;

            tmp = pe[len + i];
            pe[len + i] = pe[len + mini];
            pe[len + mini] = tmp;
        }

        return curry(Arrays.copyOfRange(pe, len, pe.length));
    }
}
