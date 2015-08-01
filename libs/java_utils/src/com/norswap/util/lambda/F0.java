package com.norswap.util.lambda;

@FunctionalInterface
public interface F0<R>
{
    R apply();

    class CurryF0<R>
    {

    }
}
