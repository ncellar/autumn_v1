package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

/**
 * Attempts to match the next input character to a range of characters.
 *
 * Succeeds if the next input character is the range.
 *
 * On success, the end position is start position + 1.
 *
 */
public final class CharRange extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public char start;
    public char end;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        char c = parser.text.charAt(input.start);

        if (start <= c && c <= end)
        {
            input.advance(1);
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
