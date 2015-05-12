package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

import static com.norswap.autumn.parsing.Registry.PH_DEPTH;

public final class Trace extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        Integer depth = parser.ext.get(PH_DEPTH);

        if (depth == null)
        {
            depth = 0;
            parser.ext.set(PH_DEPTH, 0);
        }

        System.err.println(new String(new char[depth]).replace("\0", "-|") + operand);

        parser.ext.set(PH_DEPTH, depth + 1);
        operand.parse(parser, input);
        parser.ext.set(PH_DEPTH, depth);
    }

    // ---------------------------------------------------------------------------------------------

    public int parseDumb(CharSequence text, int position)
    {
        return operand.parseDumb(text, position);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        operand.toString(builder);
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
