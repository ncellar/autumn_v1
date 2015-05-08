package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseOutput;
import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class ZeroMore extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        final ParseInput down = new ParseInput(input);
        final ParseOutput up = down.output;
        down.setCuttable();

        ParseOutput farthestOutput = new ParseOutput(input);

        while (true)
        {
            operand.parse(parser, down);

            if (up.failed())
            {
                break;
            }

            down.advance(up);
            farthestOutput.become(up);
            up.unCut();
        }

        if (up.isCut())
        {
            parser.fail(this, input);
        }
        else
        {
            input.output.become(farthestOutput);
        }
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
