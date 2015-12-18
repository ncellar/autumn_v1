package com.norswap.autumn.test.languages;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.Grammar;
import com.norswap.autumn.ParseResult;
import com.norswap.autumn.source.Source;
import com.norswap.autumn.test.languages.clike.CLikeExtension;
import java.io.IOException;

public final class CLikeTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String
        grammarFile = "grammars/CLike.autumn",
        testFile = "src/com/norswap/autumn/test/languages/clike/Test.clike";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromFile(grammarFile).columnStart(1).build())
            .withExtension(new CLikeExtension())
            .build();

        ParseResult result = Autumn.parseSource(grammar, Source.fromFile(testFile).columnStart(1).build());

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
