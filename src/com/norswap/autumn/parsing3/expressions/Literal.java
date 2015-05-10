package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Literal extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String string;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        int index = 0;
        int pos = input.start;
        final int len = string.length();

        while (index < len && parser.text.charAt(pos) == string.charAt(index))
        {
            ++index;
            ++pos;
        }

        if (index == len)
        {
            input.advance(len);
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
        int index = 0;
        int pos = position;
        int len = string.length();

        while (index < len && text.charAt(pos) == string.charAt(index))
        {
            ++index;
            ++pos;
        }

        return index != string.length()
            ? -1
            : pos;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("\"");
        builder.append(string);
        builder.append("\"");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
