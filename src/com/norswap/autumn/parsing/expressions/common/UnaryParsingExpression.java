package com.norswap.autumn.parsing.expressions.common;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.graph.Nullability;

/**
 * Base implementation for parsing expression with a single operand.
 */
public abstract class UnaryParsingExpression extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] children()
    {
        return operand != null
            ? new ParsingExpression[]{operand}
            : new ParsingExpression[0];
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        this.operand = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        return operand != null
            ? Nullability.single(this, operand)
            : Nullability.no(this);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] firsts(Grammar grammar)
    {
        return operand != null
            ? new ParsingExpression[]{operand}
            : new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
