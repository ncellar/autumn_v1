package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing.Registry.PIH_STACK_TRACE;

/**
 *
 */
public class StackTrace extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        Array<ParsingExpression> stackTrace = input.ext.get(PIH_STACK_TRACE);

        if (stackTrace == null)
        {
            stackTrace = new Array<>();
            input.ext.set(PIH_STACK_TRACE, stackTrace);
        }

        stackTrace.push(this);

        try {
            operand.parse(parser, input);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage() + " at: ");
            for (ParsingExpression pe: stackTrace.reverseIterable())
            {
                System.err.println(pe);
            }

            throw new Error(e);
        }
        stackTrace.pop();
    }

    // ---------------------------------------------------------------------------------------------

    public int parseDumb(CharSequence text, int position)
    {
        return operand.parseDumb(text, position);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        operand.toString(builder);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return new ParsingExpression[]{operand};
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression expr)
    {
        operand = expr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
