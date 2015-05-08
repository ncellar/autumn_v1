package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Cuttable extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;
    public ParsingExpression[] operands;


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, input);

            if (input.output.succeeded())
            {
                return;
            }
            else if (input.cuts.remove(name))
            {
                break;
            }
            else
            {
                input.resetOutput();
            }
        }

        parser.fail(this, input);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(CharSequence text, int position)
    {
        for (ParsingExpression operand : operands)
        {
            int result = operand.parseDumb(text, position);

            if (result != - 1)
            {
                return result;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("cuttable(");
        builder.append("\"");
        builder.append(name);
        builder.append("\", ");

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
