package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.util.StringEscape;

/**
 * Attempts to match the next input character to a range of characters.
 *
 * Succeeds if the next input character is the set.
 *
 * On success, the end position is start position + 1.
 */
public final class CharSet extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public char[] chars;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        char c = parser.text.charAt(input.start);

        for (char d : chars)
        {
            if (c == d)
            {
                input.advance(1);
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
