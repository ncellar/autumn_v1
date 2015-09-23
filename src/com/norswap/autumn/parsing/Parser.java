package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.ErrorHandler;
import com.norswap.autumn.parsing.config.MemoHandler;
import com.norswap.autumn.parsing.config.ParserConfiguration;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.tree.BuildParseTree;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Grammar grammar;

    public final Source source;

    public final CharSequence text;

    public final Object[] scoped;

    private final CustomState[] customStates;

    public final ErrorHandler errorHandler;

    public final ParsingExpression whitespace;

    public final MemoHandler memoHandler;

    public final boolean processLeadingWhitespace;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult parse(Grammar grammar, Source source)
    {
        return new Parser(grammar, source, ParserConfiguration.build()).parse(grammar.root());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseResult parse(Grammar grammar, Source source, ParserConfiguration config)
    {
        return new Parser(grammar, source, config).parse(grammar.root());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Grammar grammar, Source source, ParserConfiguration config)
    {
        this.grammar = grammar;
        this.source = source;
        this.text = source.text;
        this.scoped = config.scoped();
        this.customStates = config.customStates();
        this.errorHandler = config.errorHandler();
        this.memoHandler = config.memoHandler();
        this.whitespace = grammar.whitespace();
        this.processLeadingWhitespace = grammar.processLeadingWhitespace();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Use the parser to match its source text to the given parsing expression.
     *
     * After calling this method, the parse tree resulting from the parse can be retrieved via
     * {@blink #tree()}.
     *
     * If the parse failed ({@code failed() == true}) or a partial match ({@code
     * matchedWholeSource() == false}), errors can be reported with report().
     *
     * TODO change
     */
    public ParseResult parse(ParsingExpression pe)
    {
        ParseState rootState = new ParseState(null, customStates); // TODO
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

        // TODO
        return new ParseResult(end == source.length(), end >= 0, end, tree.build(), null, errorHandler.error(source));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method should be called whenever a parsing expression fails. It calls {@link
     * ParseState#fail} and passes the error to the error handler.
     * <p>
     * {@code state} should be in the same state as when the expression was invoked. This means
     * {@link ParseState#discard} should have been called on the state if necessary.
     * <p>
     * In some cases, an expression may elect not to report a failure, in which case it must call
     * {@link ParseState#fail} directly instead (e.g. left-recursion for blocked recursive calls).
     */
    public void fail(ParsingExpression pe, ParseState state)
    {
        state.fail();

        if (!state.isErrorRecordingForbidden())
        {
            errorHandler.handle(pe, state);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
