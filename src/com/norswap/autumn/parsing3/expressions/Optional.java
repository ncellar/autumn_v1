package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Optional extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        operand.parse(parser, input);

        if (input.output.failed())
        {
            input.resetOutput();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        int result = operand.parseDumb(text, position);

        return result != -1
            ? result
            : position;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("optional(");
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
