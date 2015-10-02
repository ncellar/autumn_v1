package com.norswap.autumn.parsing.state.errors;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.source.TextPosition;
import com.norswap.util.Array;

/**
 * Default implementation of {@link ErrorReport}, used by {@link DefaultErrorState}.
 * <p>
 * Reports the tokens which we expected at the farthest error position, but failed to match.
 */
public final class DefaultErrorReport implements ErrorReport
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final String message;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DefaultErrorReport(TextPosition farthestErrorPosition, Array<ParsingExpression> farthestExpressions)
    {
        StringBuilder b = new StringBuilder();

        b.append("The parser failed to match any of the following expressions at position ");
        b.append(farthestErrorPosition);
        b.append(":\n");

        for (ParsingExpression farthestExpression: farthestExpressions)
        {
            b.append(farthestExpression);
            b.append("\n");
        }

        this.message = b.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String message()
    {
        return message;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
