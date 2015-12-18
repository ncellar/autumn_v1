package com.norswap.autumn.graph;

import com.norswap.autumn.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph.GraphVisitor;

/**
 * A graph visitor that visits all nodes in a parsing expression graph.
 */
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
