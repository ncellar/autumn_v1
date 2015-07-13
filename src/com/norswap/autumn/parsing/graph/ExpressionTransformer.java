package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * TODO
 */
@FunctionalInterface
public interface ExpressionTransformer
{
    ParsingExpression transform(ParsingExpression pe);
}
