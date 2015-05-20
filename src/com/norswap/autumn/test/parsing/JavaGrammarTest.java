package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.support.GrammarDriver;
import com.norswap.autumn.util.Glob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public final class JavaGrammarTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "src/com/norswap/autumn/test/grammars/Java8.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        ParsingExpression[] rules = GrammarDriver.compile(grammarFile);
        ParsingExpression root = rules[0];

        ParsingExpression whitespace = Arrays.stream(rules)
            .filter(rule -> "Spacing".equals(rule.name()))
            .findFirst().get();

        try {
            for (Path path: Glob.glob("**/*.java", new File("../guava").toPath()))
            {
                ParserConfiguration config = new ParserConfiguration();
                config.whitespace = whitespace;

                parseFile(path.toString(), root, config);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static void parseFile(
        String filename, ParsingExpression root, ParserConfiguration config)
    {
        try
        {
            Source source = Source.fromFile(filename);
            Parser parser = new Parser(source, config);
            root = InstrumentExpression.stackTrace(root);
            parser.parse(root);

            if (parser.succeeded())
            {
                //System.err.println(filename);
                //System.out.println(parser.tree());
            }
            else
            {
                System.err.println(filename);
                parser.report();
                System.err.println("");
                System.exit(-1);
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + filename);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
