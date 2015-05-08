package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.*;
import com.norswap.autumn.parsing3.ParseOutput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class LongestMatch extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        ParseResult oldResult = input.result;
        int oldCount = input.resultChildrenCount;

        ParseOutput farthestOutput = ParseOutput.failure();
        ParseResult longestResult = null;

        for (ParsingExpression operand : operands)
        {
            input.setResult(ParseResult.container());

            operand.parse(parser, input);

            if (input.output.position > farthestOutput.position)
            {
                farthestOutput.become(input.output);
                longestResult = input.result;
            }

            input.resetOutput();
        }

        input.result = oldResult;
        input.resultChildrenCount = oldCount;
        input.output.become(farthestOutput);
        input.merge(longestResult);

        if (input.output.failed())
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
