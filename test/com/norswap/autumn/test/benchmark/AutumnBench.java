package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.Grammar;
import com.norswap.autumn.ParseResult;
import com.norswap.autumn.source.Source;
import com.norswap.util.Array;
import com.norswap.util.Glob;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public final class AutumnBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Instant startParse = Instant.now();
        Grammar grammar = Grammar.fromSource(
            Source.fromFile(grammarFile).columnStart(1).build())
            .build();

        // inspect grammar
        //new Printer(System.err::print, false, false).visit(grammar.root());

        Instant endParse = Instant.now();
        System.out.println("Grammar compiled in: " + Duration.between(startParse, endParse));

        Array<Duration> durations = new Array<>();
        Instant start = Instant.now();
        Instant mid = start;
        int iters = 1;

        int j = 0;
        int target = 7;

        for (int i = 0; i < iters; ++i)
        {
            for (Path path: Glob.glob("**/*.java", Paths.get(
                //"/Users/nilaurent/Documents/spring-framework")))
                "../_readonly/guava")))
            {
                ParseResult result = Autumn.parseFile(grammar, path.toString());

                if (!result.matched)
                {
                    System.err.println(path);
                    System.err.println(result.error.message());

                    return;
                }

                if (++j == target)
                {
                    System.err.println(path);
                    System.err.println("-------");
                    System.err.println(result.tree);
                }

            }

            Instant tmp = Instant.now();
            durations.add(Duration.between(mid, tmp));
            mid = tmp;
        }
        Instant end = Instant.now();
        System.out.println("Code parsed in: " + Duration.between(start, end).dividedBy(iters));
        System.out.println(durations);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}