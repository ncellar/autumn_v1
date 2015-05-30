package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.Expression;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.HandleMap;
import com.norswap.autumn.util.Pair;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    public CharSequence text;

    public ParserConfiguration configuration;

    private ParseTree tree;

    private Array<LeftRecursive> blocked;

    private Array<Expression.PrecedenceEntry> minPrecedence;

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
        this.blocked = new Array<>();
        this.minPrecedence = new Array<>();

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

    /**
     * This method should be called whenever a parsing expression fails. It calls {@link
     * ParseInput#fail} and passes the error to the error handler.
     *
     * {@code input} should be in the same state as when the expression was invoked, modulo any
     * changes that persists across failures (e.g. cuts). This means {@link ParseInput#resetOutput}
     * should have been called on the input if necessary.
     *
     * In some cases, an expression may elect not to report a failure, in which case it must
     * call {@link ParseInput#fail} directly instead (e.g. left-recursion for blocked recursive
     * calls).
     */
    public void fail(ParsingExpression pe, ParseInput input)
    {
        input.fail();

        if (!input.isErrorRecordingForbidden())
        {
            configuration.errorHandler.handle(pe, input);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isBlocked(LeftRecursive lr)
    {
        for (LeftRecursive la: blocked)
        {
            if (lr == la)
            {
                return true;
            }
        }

        return false;
    }

    //----------------------------------------------------------------------------------------------

    public void pushBlocked(LeftRecursive lr)
    {
        blocked.push(lr);
    }

    //----------------------------------------------------------------------------------------------

    public void popBlocked()
    {
        blocked.pop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PRECEDENCE

    //----------------------------------------------------------------------------------------------

    /**
     * If {@code expr} is not yet in the process of being parsed, registers a precedence value of 0
     * for this expression and return it. Otherwise, returns the current precedence value for the
     * expression.
     */
    public int enterPrecedence(Expression expr, int position)
    {
        Expression.PrecedenceEntry entry = minPrecedence.peekOrNull();

        if (entry == null || entry.expression != expr)
        {
            entry = new Expression.PrecedenceEntry();
            entry.expression = expr;
            entry.initialPosition = position;
            entry.minPrecedence = 0;

            minPrecedence.push(entry);
            return 0;
        }
        else
        {
            return entry.minPrecedence;
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Returns the current precedence value for the expression being parsed most recently.
     * This is safe because inter-expression recursion is forbidden.
     */
    public int minPrecedence()
    {
        return minPrecedence.peek().minPrecedence;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Sets the current precedence value for the expression being parsed most recently.
     * This is safe because inter-expression recursion is forbidden.
     */
    public void setMinPrecedence(int precedence)
    {
        minPrecedence.peek().minPrecedence = precedence;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * If the expression being parsed most recently was entered at {@code position}, unregister
     * the expression; otherwise sets its current precedence to {@code precedence}.
     *
     * This method is necessary because non-left recursion of expressions is done by invoking the
     * expression at another position. When this call exits, it needs to restore the precedence
     * that was in effect when it was entered. The initial call needs to unregister the expression.
     */
    public void exitPrecedence(int precedence, int position)
    {
        Expression.PrecedenceEntry entry = minPrecedence.peek();

        if (entry.initialPosition == position)
        {
            minPrecedence.pop();
        }
        else
        {
            entry.minPrecedence = precedence;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
