package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.ParserConfiguration;

/**
 * Invokes {@link ParserConfiguration#whitespace} at its start position.
 *
 * Always succeeds.
 *
 * On success, its end position is the end position of the whitespace expression.
 */
public final class Whitespace extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        int end = parser.configuration.whitespace.parseDumb(parser.text, input.end);

        if (end > 0)
        {
            input.end = end;
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
