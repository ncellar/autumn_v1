package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

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

    private final @Immutable Map<Class<? extends Extension>, Integer> indices = new HashMap<>();

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

        int size = grammar.extensions.size();
        for (int i = 0; i < size; ++i)
        {
            indices.put(grammar.extensions.get(i).getClass(), i);
        }
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
        state = new ParseState(
            config.errorState(),
            config.memoHandler(),
            grammar.extensions.mapToArray(Extension::customParseState, CustomState[]::new));

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
            state.tree.build(),
            Array.map(state.customStates, x -> x.extract(state)),
            state.errors.report(source));

        if (state.end < 0)
        {
            state.discard();
        }

        state = null;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the custom state associated with the given extension. This is generally called from a
     * custom parsing expression to acquire a reference to the relevant custom state on-demand. The
     * returned state should be cached to avoid the lookup overhead.
     */
    @SuppressWarnings("unchecked")
    public <T extends CustomState> T state(Class<? extends Extension> klass)
    {
        return (T) state.customStates[indices.get(klass)];
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
