package com.norswap.autumn.test.benchmark;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.support.GrammarDriver;
import com.norswap.autumn.test.parsing.InstrumentExpression;
import com.norswap.autumn.util.Glob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class AutumnBench
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        //System.in.read();

        Instant start = Instant.now();

        ParsingExpression[] rules = GrammarDriver.compile(grammarFile);

        Instant end = Instant.now();
        System.out.println("Grammar compiled in: " + Duration.between(start, end));

        ParsingExpression root = rules[0];
        //root = InstrumentExpression.stackTrace(root);

        ParsingExpression whitespace = Arrays.stream(rules)
            .filter(rule -> "Spacing".equals(rule.name()))
            .findFirst().get();

        start = Instant.now();
        //parseDirectory("../guava/guava/src/com/google/common/base/", root, whitespace);
        parseDirectory("../guava", root, whitespace);
        end = Instant.now();
        System.out.println("Guava parsed in: " + Duration.between(start, end));
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseDirectory(
        String directory, ParsingExpression root, ParsingExpression whitespace)
        throws IOException
    {
        for (Path path: Glob.glob("**/*.java", new File(directory).toPath()))
        {
            ParserConfiguration config = new ParserConfiguration();
            config.whitespace = whitespace;

            parseFile(path.toString(), root, config);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static void parseFile(
        String file, ParsingExpression root, ParserConfiguration config)
    {
        try
        {
            Source source = Source.fromFile(file);
            Parser parser = new Parser(source, config);

            parser.parse(root);

            if (parser.succeeded())
            {
                //System.err.println(file);
                //System.out.println(parser.tree());
            }
            else
            {
                System.err.println(file);
                parser.report();
                System.err.println();
                throw new Error("stop!");
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + file);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
