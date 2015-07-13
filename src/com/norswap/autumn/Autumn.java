package com.norswap.autumn;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseException;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.LeftRecursionBreaker;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.GrammarGrammar;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class Autumn
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parseString(String grammarFile, String string)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), Source.fromFile(string));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseFile(String grammarFile, String inputFile)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), Source.fromFile(inputFile));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseSource(String grammarFile, Source source)
        throws IOException
    {
        return parseSource(grammarFromFile(grammarFile), source);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseString(Grammar grammar, String string)
    {
        return parseSource(grammar, Source.fromString(string));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseFile(Grammar grammar, String inputFile) throws IOException
    {
        return parseSource(grammar, Source.fromFile(inputFile));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parseSource(Grammar grammar, Source source)
    {
        return Parser.parse(grammar, source);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Grammar grammarFromFile(String grammarFile) throws IOException
    {
        return grammarFromSource(Source.fromFile(grammarFile));
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromString(String grammarString)
    {
        return grammarFromSource(Source.fromString(grammarString));
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromSource(Source source)
    {
        ParseResult result = Parser.parse(GrammarGrammar.grammar, source);

        if (!result.succeeded)
        {
            throw new ParseException(result.error);
        }
        else
        {
            return GrammarCompiler.compile(result.tree);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromExpression(
        ParsingExpression root,
        Collection<ParsingExpression> rules,
        ParsingExpression whitespace,
        boolean processLeadingWhitespace)
    {
        Grammar grammar = new Grammar(root, rules, whitespace, processLeadingWhitespace);
        grammar.transform(new ReferenceResolver());
        grammar.transform(new LeftRecursionBreaker(grammar));
        return grammar;
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromExpression(ParsingExpression pe, ParsingExpression whitespace)
    {
        return grammarFromExpression(pe, Collections.emptyList(), whitespace, true);
    }

    // ---------------------------------------------------------------------------------------------

    public static Grammar grammarFromExpression(ParsingExpression pe)
    {
        return grammarFromExpression(
            pe, Collections.emptyList(), Whitespace.whitespace.deepCopy(), true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
