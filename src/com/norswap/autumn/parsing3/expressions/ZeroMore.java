package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.ParseOutput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.parsing3.Seed;
import com.norswap.autumn.util.Array;

public final class ZeroMore extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        Array<Seed> oldSeeds = input.seeds;
        int oldFlags = input.flags;
        int oldCount = input.resultChildrenCount;
        int oldPos = input.position;
        int oldBPos = input.blackPosition;

        ParseOutput farthestOutput = new ParseOutput(input);

        while (true)
        {
            operand.parse(parser, input);

            if (input.output.failed())
            {
                break;
            }

            input.advance(input.output);
            farthestOutput.become(input.output);
        }

        input.output.become(farthestOutput);
        input.seeds = oldSeeds;
        input.flags = oldFlags;
        input.resultChildrenCount = oldCount;
        input.resultChildrenCount = oldCount;
        input.position = oldPos;
        input.blackPosition = oldBPos;
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
