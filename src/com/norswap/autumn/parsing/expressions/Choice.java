package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

/**
 * Invokes all its operands at its initial input position, until one succeeds.
 *
 * Succeeds iff one operand succeeds.
 *
 * On success, its end position is that of the operand that succeeded.
 */
public final class Choice extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, input);

            if (input.succeeded())
            {
                return;
            }
            else
            {
                input.resetOutput();
            }
        }

        parser.fail(this, input);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        for (ParsingExpression operand : operands)
        {
            int result = operand.parseDumb(text, position);

            if (result != - 1)
            {
                return result;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("choice(");

        for (ParsingExpression operand: operands)
        {
            operand.toString(builder);
            builder.append(", ");
        }

        if (operands.length > 0)
        {
            builder.setLength(builder.length() - 2);
        }

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
