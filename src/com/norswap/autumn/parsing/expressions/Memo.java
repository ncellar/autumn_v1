package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.*;

public final class Memo extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        if (input.isMemoizationForbidden())
        {
            operand.parse(parser, input);
            return;
        }

        OutputChanges changes = parser.configuration.memoizationStrategy.get(this, input);

        if (changes != null)
        {
            changes.mergeInto(input);
            return;
        }

        operand.parse(parser, input);
        parser.configuration.memoizationStrategy.memoize(operand, input, new OutputChanges(input));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("memo(");
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
