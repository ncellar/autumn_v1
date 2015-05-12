package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.parsing3.RecursionResolver;


/**
 * A reference to another expression. A reference can be resolved or unresolved.
 *
 * Resolved references have the PEF_RESOLVED flag set, and the referenced expression as operand.
 *
 * For details on unresolved reference, see {@link RecursionResolver}.
 *
 * TODO
 * It's debatable whether references are really necessary. They could be eliminated from the graph;
 * but they do provide a nice no-op on which flags and names can be set.
 *
 * A resolved reference simply invokes its operand at its start position.
 */
public final class Reference extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;
    public ParsingExpression[] nestedReferences;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        operand.parse(parser, input);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
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
