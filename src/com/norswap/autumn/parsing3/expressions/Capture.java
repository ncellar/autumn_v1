package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseResult;
import com.norswap.autumn.parsing3.ParseInput;
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
        if (input.isCaptureForbidden())
        {
            operand.parse(parser, input);
            return;
        }

        ParseResult oldResult = input.result;
        ParseResult newResult = new ParseResult(this, input.position);
        newResult.name = name;
        newResult.grouped = isCaptureGrouped();

        operand.parse(parser, input);

        input.result = oldResult;
        newResult.finalize(input.output);
        input.merge(newResult);
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
