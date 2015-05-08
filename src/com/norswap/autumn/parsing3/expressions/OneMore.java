package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.ParseOutput;
import com.norswap.autumn.parsing3.ParseResult;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.util.Array;

public final class OneMore extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        Array<ParseResult> oldSeeds = input.seeds;
        int oldFlags = input.flags;
        int oldCount = input.resultChildrenCount;
        int oldPos = input.position;
        int oldBPos = input.blackPosition;

        operand.parse(parser, input);

        if (input.output.failed())
        {
            parser.fail(this, input);
            return;
        }
        else
        {
            input.advance(input.output);
        }

        ParseOutput farthestOutput = new ParseOutput(input.output);

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
