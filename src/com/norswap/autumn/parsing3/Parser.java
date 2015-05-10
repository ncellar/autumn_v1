package com.norswap.autumn.parsing3;

import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing3.expressions.LeftRecursive;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.HandleMap;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    public CharSequence text;

    public ParserConfiguration configuration;

    private ParseResult result;

    private Array<LeftRecursive> leftAssociatives;

    private int finalPosition;
    
    public HandleMap ext = new HandleMap();

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
     * matchedWholeSource() == false}), errors can be reported with {@link #report()}.
     */
    public void parse(ParsingExpression pe)
    {
        this.leftAssociatives = new Array<>();
        ParseInput rootInput = ParseInput.root();
        rootInput.result = result = new ParseResult();

        if (configuration.processLeadingWhitespace)
        {
            int pos = configuration.whitespace.parseDumb(text, 0);
            rootInput.position = pos;
            rootInput.output.position = pos;
        }

        pe.parse(this, rootInput);
        this.finalPosition = rootInput.output.position;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Report the outcome of the parse (success or failure) on System.err and in case of failure,
     * the errors recorded during the parse. The reporting method for the errors is up to the
     * {@link ErrorHandler}.
     */
    public void report()
    {
        if (matchedWholeSource())
        {
            System.err.println("The parse succeeded, matching the whole source.");
        }
        else if (succeeded())
        {
            System.err.println("The parse succeeded, matching a prefix of the source, up to "
                + source.position(finalPosition));
        }
        else
        {
            System.err.println("The parse failed.");
            configuration.errorHandler.reportErrors(this);
        }

    }

    //----------------------------------------------------------------------------------------------

    public Source source()
    {
        return source;
    }

    //----------------------------------------------------------------------------------------------

    public ParseResult result()
    {
        return result;
    }

    //----------------------------------------------------------------------------------------------

    public boolean succeeded()
    {
        return finalPosition >= 0;
    }

    //----------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return finalPosition < 0;
    }

    //----------------------------------------------------------------------------------------------

    public boolean matchedWholeSource()
    {
        return finalPosition == source.length();
    }

    //----------------------------------------------------------------------------------------------

    public int finalPosition()
    {
        return finalPosition;
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
