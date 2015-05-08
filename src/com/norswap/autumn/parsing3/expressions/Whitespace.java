package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Whitespace extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        int pos = parser.configuration.whitespace.parseDumb(parser.text, input.output.position);

        if (pos > 0)
        {
            input.output.position = pos;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("whitespace");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
