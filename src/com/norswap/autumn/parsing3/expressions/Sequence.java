package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.ParseOutput;
import com.norswap.autumn.parsing3.ParsingExpression;

public class Sequence implements ParsingExpression
{
    ParsingExpression[] operands;

    @Override
    public void parse(ParsingExpression pe, ParseInput input)
    {
        final ParseInput down = new ParseInput(pe, input);
        final ParseOutput up = down.output;

        for (ParsingExpression operand : operands)
        {
            parse(operand, down);

            if (up.succeeded())
            {
                down.advance(up);
            }
            else
            {
                break;
            }
        }

        input.output.become(up);
    }
}
