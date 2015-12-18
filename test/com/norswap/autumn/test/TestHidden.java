package com.norswap.autumn.test;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.Grammar;
import com.norswap.autumn.ParseResult;
import com.norswap.autumn.extensions.tracer.TracerExtension;
import com.norswap.autumn.source.Source;

import java.io.IOException;

public final class TestHidden
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarText =
            "A = [b]? C ;" +
            "C = A [a] / [a] ;";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromString(grammarText).build())
            .withExtension(new TracerExtension())
            .build();

        ParseResult result = Autumn.parseString(grammar, "bbaa");

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }

        System.err.println(result.tree);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
