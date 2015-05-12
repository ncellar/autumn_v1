package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

/**
 * Parses its operand in dumb mode, like a non-memoizing PEG parser. In this
 * mode, most features cannot be used, including:
 *
 * - error reporting
 * - memoization & cutting
 * - left-recursion
 * - associativity
 * - precedence
 * - captures
 *
 * Dumb mode incurs less memory and run-time overhead than the regular mode, but does not
 * have any of its advanced features.
 *
 * It is not necessarily more efficient than the regular mode, because a non-optimized
 * expression cannot benefit from the advanced features such as memoization to fix its
 * performance issues.
 *
 * It is most useful to parse fairly basic combinations of terminal expressions, where the
 * overhead could otherwise be consequent.
 *
 * It is not possible for a child of a dumb expression to switch back to regular mode.
 *
 * Succeeds if its operand succeeds.
 *
 *  On success, its end position is that of its operand.
 */
public final class Dumb extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        int pos = operand.parseDumb(parser.text, input.start);

        if (pos >= 0)
        {
            input.advance(pos - input.start);
        }
        else
        {
            parser.fail(this, input);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("dumb(");
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
