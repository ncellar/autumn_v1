package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Precedence extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int NONE = 0;
    public static final int ESCAPE_PRECEDENCE = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int precedence;
    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        if (precedence > 0 && precedence < input.precedence)
        {
            // We bypass error handling: it is not expected that the input matches this expression.

            input.fail();
        }
        else
        {
            int oldFlags = input.flags;
            int oldPrecedence = input.precedence;
            input.precedence = precedence;

            if (precedence > 0)
            {
                // If a precedence level is set, calling a sub-expression at the same position with
                // another precedence might yield a different input, so don't memoize.

                input.forbidMemoization();
            }

            operand.parse(parser, input);

            input.precedence = oldPrecedence;
            input.flags = oldFlags;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        if (precedence == NONE)
        {
            builder.append("noPrecedence(");
        }
        else
        {
            builder.append("precedence(");
            builder.append(precedence);
            builder.append(", ");
        }

        operand.toString(builder);
        builder.append(")");
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