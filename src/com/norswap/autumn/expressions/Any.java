package com.norswap.autumn.expressions;

import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.ParsingExpression;

/**
 * Matches any character.
 *
 * Succeeds if the end of the input has not been reached.
 *
 * On success, the end position is start position + 1.
 */
public final class Any extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        if (parser.text.charAt(state.start) != 0)
        {
            state.advance(1);
        }
        else
        {
            state.fail(this);
        }

        char c = '\0';
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return parser.text.charAt(position) != 0
            ? position + 1
            : -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
