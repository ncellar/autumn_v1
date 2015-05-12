package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.parsing3.ParserConfiguration;

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
