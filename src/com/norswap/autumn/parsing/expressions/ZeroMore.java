package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

/**
 * Repeatedly invokes its operand over the input, until it fails. Each invocation occurs at
 * the end position of the previous one.
 *
 * Always succeeds.
 *
 * On success, its end position is the end position of the last successful invocation of its
 * operand.
 */
public final class ZeroMore extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        final ParseInput down = new ParseInput(input);

        while (true)
        {
            operand.parse(parser, down);

            if (down.failed())
            {
                down.resetOutput();
                break;
            }

            down.advance();
        }

        input.merge(down);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        int result;

        while ((result = operand.parseDumb(text, position)) != -1)
        {
            position = result;
        }

        return position;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("zeroMore(");
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
