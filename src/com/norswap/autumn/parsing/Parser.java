package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.HandleMap;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    public CharSequence text;

    public ParserConfiguration configuration;

    private ParseTree tree;

    private Array<LeftRecursive> leftAssociatives;

    private int endPosition;
    
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
     * After calling this method, the parse tree resulting from the parse can be retrieved via
     * {@link #tree()}.
     *
     * If the parse failed ({@code failed() == true}) or a partial match ({@code
     * matchedWholeSource() == false}), errors can be reported with {@link #report()}.
     */
    public void parse(ParsingExpression pe)
    {
        this.leftAssociatives = new Array<>();
        ParseInput rootInput = ParseInput.root();
        rootInput.tree = tree = new ParseTree();

        if (configuration.processLeadingWhitespace)
        {
            int pos = configuration.whitespace.parseDumb(text, 0);
            if (pos > 0)
            {
                rootInput.start = pos;
                rootInput.end = pos;
            }
        }

        pe.parse(this, rootInput);

        if ((this.endPosition = rootInput.end) < 0)
        {
            rootInput.resetAllOutput();
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Report the outcome of the parse (success or failure) on System.err and in case of failure,
     * the errors recorded during the parse. The reporting method for the errors is up to the
     * {@link ErrorHandler}.
     */
    public void report()
    {
        if (succeeded())
        {
            System.err.println("The parse succeeded.");
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

    public ParseTree tree()
    {
        return tree;
    }

    //----------------------------------------------------------------------------------------------

    public boolean succeeded()
    {
        return endPosition == source.length();
    }

    //----------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return endPosition != source.length();
    }

    //----------------------------------------------------------------------------------------------

    public int endPosition()
    {
        return endPosition;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void fail(ParsingExpression pe, ParseInput input)
    {
        input.fail();

        if (!input.isErrorRecordingForbidden())
        {
            configuration.errorHandler.handle(pe, input);
        }
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
