package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.ParseResult;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

import static com.norswap.autumn.parsing3.Registry.*; // PEF_*

public final class Capture extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;
    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        ParseResult oldResult = input.result;
        ParseResult newResult = new ParseResult();
        newResult.name = name;
        int oldCount = input.resultChildrenCount;

        input.result = newResult;
        input.resultChildrenCount = 0;

        operand.parse(parser, input);

        input.result = oldResult;
        input.resultChildrenCount = oldCount;

        if (input.succeeded())
        {
            if (isCaptureGrouped())
            {
                input.result.addGrouped(newResult);
            }
            else
            {
                input.result.add(newResult);
            }

            if (shouldCaptureText())
            {
                int end = input.blackEnd;

                newResult.value = parser.text
                    .subSequence(input.start, end)
                    .toString();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("capture(");
        builder.append(name);
        builder.append(", ");
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

    public boolean shouldCaptureText()
    {
        return (flags & PEF_CAPTURE_TEXT) != 0;
    }

    public boolean isCaptureGrouped()
    {
        return (flags & PEF_CAPTURE_GROUPED) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
