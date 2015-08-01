package com.norswap.util.lambda;

import java.util.function.BiFunction;

@FunctionalInterface
public interface F2<R, T1, T2> extends BiFunction<R, T1, T2>
{
}
