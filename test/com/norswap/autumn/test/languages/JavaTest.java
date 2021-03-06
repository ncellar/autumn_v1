package com.norswap.autumn.test.languages;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.Grammar;
import com.norswap.autumn.ParseResult;
import com.norswap.autumn.source.Source;
import com.norswap.util.Glob;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class JavaTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Source source = Source.fromFile(grammarFile).columnStart(1).build();
        Grammar grammar = Grammar.fromSource(source).build();

        for (Path path: Glob.glob("**/*.java", Paths.get("../_readonly/guava")))
        {
            ParseResult result = Autumn.parseFile(grammar, path.toString());

            if (!result.matched)
            {
                System.err.println(path);
                System.err.println(result.error.message());

                return;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
