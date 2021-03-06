package com.norswap.autumn.test;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.Grammar;
import com.norswap.autumn.ParseResult;
import com.norswap.autumn.source.Source;

import java.io.IOException;

public final class TestGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromFile(grammarFile).build()).build();

        ParseResult result = Autumn.parseFile(grammar, "src/com/norswap/autumn/test/grammars/Syntax.test");

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }

        System.err.println(result.tree);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

