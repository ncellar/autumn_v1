package com.norswap.autumn.test.parsing;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.graph.Walks;
import com.norswap.util.Glob;
import com.norswap.util.graph_visit.GraphTransformer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class JavaGrammarTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Autumn.grammarFromFile(grammarFile);

        for (Path path: Glob.glob("**/*.java", Paths.get("../guava")))
        {
            ParseResult result = Autumn.parseFile(grammar, path.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
