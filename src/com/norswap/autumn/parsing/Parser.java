package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.MemoHandler;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.CustomState.Result;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.JArrays;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Grammar grammar;

    public final Source source;

    public final CharSequence text;

    public final Object[] scoped;

    private final CustomState[] customStates;

    public final ErrorState errorState;

    public final ParsingExpression whitespace;

    public final MemoHandler memoHandler;

    public final boolean processLeadingWhitespace;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parse(Grammar grammar, Source source)
    {
        return new Parser(grammar, source, ParserConfiguration.build()).parse(grammar.root);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parse(Grammar grammar, Source source, ParserConfiguration config)
    {
        return new Parser(grammar, source, config).parse(grammar.root);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Grammar grammar, Source source, ParserConfiguration config)
    {
        this.grammar = grammar;
        this.source = source;
        this.text = source.text;
        this.scoped = config.scoped();
        this.customStates = config.customStates();
        this.errorState = config.errorState();
        this.memoHandler = config.memoHandler();
        this.whitespace = grammar.whitespace;
        this.processLeadingWhitespace = grammar.processLeadingWhitespace;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseResult parse(ParsingExpression pe)
    {
        ParseState rootState = new ParseState(errorState, customStates);
        BuildParseTree tree = rootState.tree;

        if (processLeadingWhitespace)
        {
            int pos = whitespace.parseDumb(this, 0);
            if (pos > 0)
            {
                rootState.start = pos;
                rootState.end = pos;
            }
        }

        pe.parse(this, rootState);

        int end = rootState.end;

        if (end < 0)
        {
            rootState.discard();
        }

        return new ParseResult(
            end == source.length(),
            end >= 0,
            end,
            tree.build(),
            JArrays.map(customStates, Result[]::new, CustomState::result),
            errorState.report(source));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
