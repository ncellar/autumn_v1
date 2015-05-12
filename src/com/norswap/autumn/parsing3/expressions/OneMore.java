package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

/**
 * Repeatedly invokes its operand over the input, until it fails.
 *
 * Succeeds if its operand succeeded at least once.
 *
 * On success, its end position is the end position of the last successful
 * invocation of its operand.
 */
public final class OneMore extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        final ParseInput down = new ParseInput(input);
        operand.parse(parser, down);

        if (down.failed())
        {
            parser.fail(this, input);
            return;
        }
        else
        {
            down.advance();
        }

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
        position = operand.parseDumb(text, position);

        if (position == -1)
        {
            return -1;
        }

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
        builder.append("oneMore(");
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
