package com.norswap.autumn.expressions.abstrakt;

import com.norswap.autumn.ParsingExpression;

/**
 *  Base implementation for parsing expression with an array of operands.
 */
public abstract class NaryParsingExpression extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression[] operands;

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return operands;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void copyOwnData()
    {
        operands = operands.clone();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression pe)
    {
        operands[position] = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
