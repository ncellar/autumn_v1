package com.norswap.autumn .parsing.debugger;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.debugger.store.Breakpoint;
import com.norswap.autumn.parsing.debugger.store.DebuggerStore;
import com.norswap.autumn.parsing.graph.Transformer;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.util.Array;

import java.io.IOException;

/**
 *
 */
public final class Main
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        // TODO
        args = new String[]{ "bogus", "bogus" };

        if (args.length < 2)
        {
            System.err.println(
                "When invoked directly, the debugger takes two parameters: the grammar file and the source file.");
        }

        DebuggerStore store = new DebuggerStore();

        Grammar grammar;
        Source source;

        try {
            grammar = Grammar
                .fromSource(Source.fromFile(args[0]).build())
                .transform(new Transformer(pe -> new Breakpoint(pe, store)))
                .build();
        }
        catch (IOException e)
        {
            System.err.println("Could not create grammar: " + e.getMessage());
            return;
        }

        try {
            source = Source.fromFile(args[1]).build();
        }
        catch (IOException e)
        {
            System.err.println("Could not get source file: " + e.getMessage());
            return;
        }

        ParserConfiguration config = ParserConfiguration.build();
        Parser parser = new Parser(grammar, source, config);
        Debugger debugger = new Debugger(parser, store);
        ExecutionLocation rootLocation = new ExecutionLocation(grammar.root);
        store.debugger = debugger;

        store.windowFor(
                rootLocation,
                new Array<>(),
                parser.rootInputs());

        // TODO start GUI
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
