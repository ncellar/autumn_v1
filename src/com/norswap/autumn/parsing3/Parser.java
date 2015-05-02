package com.norswap.autumn.parsing3;

import com.norswap.autumn.parsing.Source;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    private CharSequence text;

    private ParserConfiguration configuration;

    private ParseResult result;

    private int depth = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Source source, ParserConfiguration configuration)
    {
        this.source = source;
        this.text = source.text();
        this.configuration = configuration;
    }

    public Parser(Source source)
    {
        this(source, ParserConfiguration.DEFAULT);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Use the parser to match its source text to the given parsing expression.
     *
     * After calling this method, the result of the parse can be retrieved via {@link #result()}.
     *
     * If the result is a failure ({@code result().failed() == true}) or a partial match ({@code
     * matchedWholeSource() == false}), errors can be reported with {@link #reportErrors()}.
     */
    public void parse(ParsingExpression pe)
    {
        ParseInput rootInput = ParseInput.root();
        rootInput.result = result = new ParseResult(pe, 0);
        parse(pe, rootInput);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Report the errors recorded during the parse. The reporting method is up to the {@link
     * ErrorHandler}.
     */
    public void reportErrors()
    {
        configuration.errorHandler.report(source);
    }

    //----------------------------------------------------------------------------------------------

    public ParseResult result()
    {
        return result;
    }

    //----------------------------------------------------------------------------------------------

    public boolean matchedWholeSource()
    {
        return !result.failed() && result.endPosition() == source.length();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
