package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

/**
 * Invokes all its operands sequentially over the input, until one fails. Each operand is
 * invoked at the end position of the previous one.
 *
 * Succeeds iff all operands succeed.
 *
 * On success, its end position is that of its last operand.
 */
public final class Sequence extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        ParseInput down = new ParseInput(input);

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, down);

            if (down.succeeded())
            {
                down.advance();
            }
            else
            {
                input.resetOutput();
                parser.fail(this, input);
                return;
            }
        }

        input.merge(down);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        for (ParsingExpression operand: operands)
        {
            position = operand.parseDumb(text, position);

            if (position == -1)
            {
                break;
            }
        }

        return position;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("sequence(");

        for (ParsingExpression operand: operands)
        {
            operand.toString(builder);
            builder.append(", ");
        }

        builder.setLength(builder.length() - 2);
        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return operands;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression expr)
    {
        operands[position] = expr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}