package com.norswap.autumn;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseException;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Replacer;
import com.norswap.autumn.parsing.graph.LeftRecursionDetector;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.GrammarGrammar;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * A collection of entry points into the library for the most common tasks.
 */
public final class Autumn
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE A STRING
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseString(Grammar grammar, String string)
    {
        return parseSource(
            grammar,
            Source.fromString(string).build(),
            ParserConfiguration.build());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseString(Grammar grammar, String string, ParserConfiguration config)
    {
        return parseSource(
            grammar,
            Source.fromString(string).build(),
            config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE A FILE
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseFile(Grammar grammar, String inputFile)
        throws IOException
    {
        return parseSource(
            grammar,
            Source.fromFile(inputFile).build(),
            ParserConfiguration.build());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseFile(Grammar grammar, String inputFile, ParserConfiguration config)
        throws IOException
    {
        return parseSource(
            grammar,
            Source.fromFile(inputFile).build(),
            config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSE A SOURCE OBJECT
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseSource(Grammar grammar, Source source)
    {
        return parseSource(
            grammar,
            source,
            ParserConfiguration.build());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseSource(Grammar grammar, Source source, ParserConfiguration config)
    {
        return Parser.parse(grammar, source, config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MAKE GRAMMARS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Grammar grammarFromFile(String grammarFile) throws IOException
    {
        return grammarFromSource(Source.fromFile(grammarFile).build());
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromString(String grammarString)
    {
        return grammarFromSource(Source.fromString(grammarString).build());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Parses and compiles the grammar specification contained in the passed source.
     *
     * TODO exceptions
     */
    public static Grammar grammarFromSource(Source source)
    {
        ParseResult result = Parser.parse(GrammarGrammar.grammar, source);

        if (!result.matched)
        {
            throw new ParseException(result.error);
        }
        else
        {
            return GrammarCompiler.compile(result.tree);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromExpression(ParsingExpression pe, ParsingExpression whitespace)
    {
        return grammarFromExpression(pe, Collections.emptyList(), whitespace, true, true);
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromExpression(ParsingExpression pe)
    {
        return grammarFromExpression(pe, Collections.emptyList(), Whitespace.DEFAULT(), true, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Make a new grammar given a root, a collection of rules, a whitespace expression and an
     * indication whether to match whitespace at the start of the input.
     * <p>
     * The rule set can be empty. The semantics are the same whether the rule set include the root
     * and the whitespace expression (or either) or not.
     *
     * TODO preprocess in grammar
     */
    public static Grammar grammarFromExpression(
        ParsingExpression root,
        Collection<ParsingExpression> rules,
        ParsingExpression whitespace,
        boolean processLeadingWhitespace,
        boolean preprocess)
    {
        Grammar grammar = new Grammar(root, rules, whitespace, processLeadingWhitespace);

        if (preprocess)
        {
            grammar.walk(new ReferenceResolver());

            grammar.computeNullability();

            LeftRecursionDetector detector = new LeftRecursionDetector(grammar);
            grammar.walk(detector);

            grammar.walk(new Replacer(detector.leftRecursives));
        }

        return grammar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
