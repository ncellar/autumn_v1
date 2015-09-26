package com.norswap.autumn.parsing.graph2;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph2.GraphVisitor;

public abstract class ParsingExpressionVisitor extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected Iterable<ParsingExpression> children(ParsingExpression pe)
    {
        return Array.fromArray(pe.children());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
