package com.norswap.autumn.util;

public class Pair<X, Y> implements Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public X a;
    public Y b;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Pair(X a, Y b)
    {
        this.a = a;
        this.b = b;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !(object instanceof Pair)) {
            return false;
        }

        Pair<?, ?> other = (Pair<?, ?>) object;

        if (a == null && other.a != null || b == null && other.b != null) {
            return false;
        }

        return (a == null || a.equals(other.a)) && (b == null || b.equals(other.b));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "(" + a + ", " + b + ")";
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Pair<X, Y> clone()
    {
        return (Pair<X, Y>) Exceptions.rt(() -> super.clone());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
