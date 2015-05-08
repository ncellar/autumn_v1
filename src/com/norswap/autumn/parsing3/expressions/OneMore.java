package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.ParseOutput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class OneMore extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        final ParseInput down = new ParseInput(input);
        final ParseOutput up = down.output;

        operand.parse(parser, down);

        if (up.failed())
        {
            parser.fail(this, input);
            return;
        }
        else
        {
            down.advance(up);
        }

        ParseOutput farthestOutput = new ParseOutput(up);

        while (true)
        {
            operand.parse(parser, down);

            if (up.failed())
            {
                break;
            }

            down.advance(up);
            farthestOutput.become(up);
        }

        input.output.become(farthestOutput);

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
