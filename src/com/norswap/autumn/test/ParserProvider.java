package com.norswap.autumn.test;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.Tracer;

/**
 * TODO Just make a default config available.
 */
public final class ParserProvider
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Tracer tracer = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Tracer tracer()
    {
        return tracer;
    }

    public static void setTracer(Tracer tracer)
    {
        ParserProvider.tracer = tracer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void treeTrace()
    {
        setTracer(Tracer.treeTracer);
    }

    public static void noTrace()
    {
        setTracer(null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Parser parser(Source src)
    {
        ParserConfiguration config = new ParserConfiguration();
        config.debug = true;
        config.tracer = tracer;
        return new Parser(src, config);
    }

    public static Parser parser(String src)
    {
        return parser(Source.fromString(src));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
