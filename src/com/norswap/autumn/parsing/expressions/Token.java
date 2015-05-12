package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

public final class Token extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        operand.parse(parser, input);

        if (input.failed())
        {
            return;
        }

        int pos = parser.configuration.whitespace.parseDumb(parser.text, input.end);

        if (pos > 0)
        {
            input.end = pos;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("token(");
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
