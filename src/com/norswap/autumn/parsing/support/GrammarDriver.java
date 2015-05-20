package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.ReferenceResolver;
import com.norswap.autumn.parsing.Source;

import java.io.IOException;

public final class GrammarDriver
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final static ParserConfiguration config = new ParserConfiguration();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Parse the grammar file whose path is supplied and return an array of parsing expressions
     * corresponding to the grammar rules defined in the grammar. The resolvable references in these
     * rules have been resolved.
     *
     * It is conventional to have the root of the grammar be the first expression.
     */
    public static ParsingExpression[] compile(String grammarFile) throws IOException
    {
        Source source = Source.fromFile(grammarFile);
        Parser parser = new Parser(source, config);
        parser.parse(GrammarGrammar.grammar);

        if (!parser.succeeded())
        {
            parser.report();
            throw new RuntimeException("Failed to parse the grammar file.");
        }
        else
        {
            return new ReferenceResolver().resolve(new GrammarCompiler().compile(parser.tree()));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
