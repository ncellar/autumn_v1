package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.*;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

/**
 * Invokes all its operands at its initial input position.
 *
 * Succeeds if at least one of its operands succeeds.
 *
 * On success, its end position is the largest amongst the end positions of its
 * successful operands.
 */
public final class LongestMatch extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        OutputChanges farthestChanges = OutputChanges.failure();

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, input);

            if (input.end > farthestChanges.end)
            {
                farthestChanges = new OutputChanges(input);
            }

            input.resetAllOutput();
        }

        farthestChanges.mergeInto(input);

        if (input.failed())
        {
            parser.fail(this, input);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        int farthestPosition = -1;

        for (ParsingExpression operand: operands)
        {
            int result = operand.parseDumb(text, position);

            if (result > farthestPosition)
            {
                farthestPosition = result;
            }
        }

        return farthestPosition;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("longestMatch(");

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
