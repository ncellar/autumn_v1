package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

public final class Parser implements Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Grammar grammar;

    public final Source source;

    public final CharSequence text;

    public final ParsingExpression whitespace;

    public final ParserConfiguration config;

    public final boolean processLeadingWhitespace;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParseState state;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Grammar grammar, Source source, ParserConfiguration config)
    {
        this.grammar = grammar;
        this.source = source;
        this.text = source.text;
        this.config = config;
        this.whitespace = grammar.whitespace;
        this.processLeadingWhitespace = grammar.processLeadingWhitespace;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invokes the root of the grammar at the start of the input and returns the result.
     */
    public ParseResult parseRoot()
    {
        return parse(null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Parses the source using the supplied inputs and returns the result.
     */
    public ParseResult parse(ParseInputs inputs)
    {
        Array<CustomState> indexedStates = new Array<>();

        for (Extension extension: grammar.extensions)
        {
            int index = extension.stateIndex();

            if (index != -1) {
                indexedStates.put(index, extension.customParseState());
            }
        }

        state = new ParseState(
            config.errorState(),
            config.memoHandler(),
            indexedStates.toArray(CustomState[]::new));

        if (inputs != null)
        {
            state.load(inputs);
            if (inputs.start() == 0) processLeadingWhitespace(state);
            inputs.pe().parse(this, state);
        }
        else
        {
            processLeadingWhitespace(state);
            grammar.root.parse(this, state);
        }

        ParseResult out = new ParseResult(
            state.end == source.length(),
            state.end >= 0,
            state.end,
            new ParseTree(state.tree.build()[0]),
            Array.map(state.customStates, x -> x == null ? null : x.extract(state)),
            state.errors.report(source));

        if (state.end < 0)
        {
            state.discard();
        }

        state = null;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    private void processLeadingWhitespace(ParseState state)
    {
        if (processLeadingWhitespace)
        {
            int pos = whitespace.parseDumb(this, 0);
            if (pos > 0)
            {
                state.start = pos;
                state.end = pos;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Parser clone()
    {
        try {
            return (Parser) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new Error(); // shouldn't happen
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
