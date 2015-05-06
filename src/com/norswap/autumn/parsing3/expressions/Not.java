package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Not extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        int flags = input.flags;
        ParseInput parentCuttable = input.parentCuttable;

        input.forbidCapture();
        input.forbidErrorRecording();
        input.isolateCuts();

        operand.parse(parser, input);

        if (input.output.succeeded())
        {
            parser.fail(this, input);
        }
        else
        {
            input.resetOutput();
        }

        input.flags = flags;
        input.parentCuttable = parentCuttable;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        return operand.parseDumb(text, position) == -1
            ? position
            : -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("not(");
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
}
