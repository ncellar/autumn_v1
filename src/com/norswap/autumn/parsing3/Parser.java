package com.norswap.autumn.parsing3;

import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing3.expressions.Capture;
import com.norswap.autumn.parsing3.expressions.LeftRecursive;
import com.norswap.autumn.util.Array;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    public CharSequence text;

    public ParserConfiguration configuration;

    private ParseResult result;

    private Array<LeftRecursive> leftAssociatives;

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
    public void parse(Capture pe)
    {
        this.leftAssociatives = new Array<>();
        ParseInput rootInput = ParseInput.root();
        rootInput.result = result = new ParseResult(pe, 0);

        if (configuration.processLeadingWhitespace)
        {
            int pos = configuration.whitespace.parseDumb(text, 0);
            rootInput.position = pos;
            rootInput.output.position = pos;
        }

        pe.parse(this, rootInput);
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

    public void fail(ParsingExpression pe, ParseInput input)
    {
        input.output.fail();

        if (!input.isErrorRecordingForbidden())
        {
            configuration.errorHandler.handle(pe, input.position);
        }

        input.resetResultChildren();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isLeftAssociative(LeftRecursive lr)
    {
        for (LeftRecursive la: leftAssociatives)
        {
            if (lr == la)
            {
                return true;
            }
        }

        return false;
    }

    //----------------------------------------------------------------------------------------------

    public void pushLeftAssociative(LeftRecursive lr)
    {
        leftAssociatives.push(lr);
    }

    //----------------------------------------------------------------------------------------------

    public void popLeftAssociative()
    {
        leftAssociatives.pop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
