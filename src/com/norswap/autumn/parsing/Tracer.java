package com.norswap.autumn.parsing;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface Tracer extends BiConsumer<ParsingExpression, Integer>
{
    static Tracer treeTracer = (pe, depth) ->
    {
        System.err.println(new String(new char[depth]).replace("\0", "-|") + pe);
    };
}
