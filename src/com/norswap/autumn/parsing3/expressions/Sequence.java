package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.parsing3.Seed;
import com.norswap.autumn.util.Array;

public final class Sequence extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    @Override
    public void parse(Parser parser, ParseInput input)
    {
        final ParseInput down = new ParseInput(input);
        final ParseOutput up = down.output;

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, down);

            if (up.succeeded())
            {
                down.advance(up);
            }
            else
            {
                parser.fail(this, input);
                return;
            }
        }

        input.output.become(up);
    }
    //*/

    //*
    @Override
    public void parse(Parser parser, ParseInput input)
    {
        Array<Seed> oldSeeds = input.seeds;
        int oldFlags = input.flags;
        int oldCount = input.resultChildrenCount;
        int oldPos = input.position;
        int oldBPos = input.blackPosition;

        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, input);

            if (input.output.succeeded())
            {
                input.advance(input.output);
            }
            else
            {
                input.seeds = oldSeeds;
                input.flags = oldFlags;
                input.resultChildrenCount = oldCount;
                input.position = oldPos;
                input.blackPosition = oldBPos;
                parser.fail(this, input);
                return;
            }
        }

        input.seeds = oldSeeds;
        input.flags = oldFlags;
        input.resultChildrenCount = oldCount;
        input.resultChildrenCount = oldCount;
        input.position = oldPos;
        input.blackPosition = oldBPos;
    }
    //*/

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