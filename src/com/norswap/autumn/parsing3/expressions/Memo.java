package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.*;

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

        ParseResult memoed = parser.configuration.memoizationStrategy.get(this, input.position);

        if (memoed != null)
        {
            input.load(memoed);
            return;
        }

        ParseResult oldResult = input.result;
        ParseResult newResult = ParseResult.container();
        input.result = newResult;

        operand.parse(parser, input);

        input.result = oldResult;
        newResult.finalize(input.output);
        input.merge(newResult);
        parser.configuration.memoizationStrategy.memoize(newResult);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
