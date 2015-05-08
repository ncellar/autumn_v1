package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class CharRange extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public char start;
    public char end;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        char c = parser.text.charAt(input.position);

        if (start <= c && c <= end)
        {
            input.output.advance(1);
        }
        else
        {
            parser.fail(this, input);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        char c = text.charAt(position);

        return start <= c && c <= end
            ? position + 1
            : -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("charRange(");
        builder.append(start);
        builder.append(", ");
        builder.append(end);
        builder.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
