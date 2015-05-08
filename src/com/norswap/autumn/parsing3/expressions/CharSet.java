package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.util.StringEscape;

public final class CharSet extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public char[] chars;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        char c = parser.text.charAt(input.position);

        for (char d : chars)
        {
            if (c == d)
            {
                input.output.advance(1);
                return;
            }
        }

        parser.fail(this, input);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        char c = text.charAt(position);

        for (char d : chars)
        {
            if (c == d)
            {
                return position + 1;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("charSet(\"");
        builder.append(StringEscape.escape(new String(chars)));
        builder.append("\")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
