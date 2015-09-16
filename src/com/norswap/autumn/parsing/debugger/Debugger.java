package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.Registry;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.io.IOException;

public final class Debugger
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int DEBUG_HANDLE = Registry.ParserHandleFactory.next();
    public static final int DEBUG_WINDOW = Registry.ParserHandleFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Grammar grammar;
    public final Source source;
    public final ParserConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        args = new String[]{ "bogus", "bogus" };
        if (args.length < 2)
        {
            System.err.println(
                "When invoked directly, the debugger takes two parameters: the grammar file and the source file.");
        }

        Grammar grammar;
        Source source;

        try {
            grammar = Autumn.grammarFromFile(args[0]);
        }
        catch (IOException e)
        {
            System.err.println("Could not create grammar: " + e.getMessage());
            return;
        }

        try {
            source = Source.fromFile(args[1]);
        }
        catch (IOException e)
        {
            System.err.println("Could not get source file: " + e.getMessage());
            return;
        }

        new Debugger(grammar, source).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Debugger(Grammar grammar, Source source)
    {
        this(grammar, source, ParserConfiguration.DEFAULT);
    }

    // ---------------------------------------------------------------------------------------------

    public Debugger(Grammar grammar, Source source, ParserConfiguration config)
    {
        this.grammar = grammar;
        this.source = source;
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void start()
    {
        // TODO fibers
        WindowModel window = new WindowModel(new ExecutionLocation(grammar.root()));
        parse(window, grammar.root());
    }

    // ---------------------------------------------------------------------------------------------

    // TODO need input
    public void parse(WindowModel window, ParsingExpression pe)
    {
        Parser parser = new Parser(grammar, source, config);
        parser.set(DEBUG_HANDLE, this);
        parser.set(DEBUG_WINDOW, window);
        ParseResult result = parser.parse(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
