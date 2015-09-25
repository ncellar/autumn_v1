package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.DefaultErrorState;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;

/**
 * Changes to the error state returned by {@link DefaultErrorState#changes}.
 */
public final class ErrorChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;
    public final Array<ParsingExpression> expressions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ErrorChanges(int position, Array<ParsingExpression> expressions)
    {
        this.position = position;
        this.expressions = expressions;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
