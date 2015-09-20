package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.ParseError;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.expressions.Token;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;

import static com.norswap.autumn.parsing.Registry.PEF_ERROR_RECORDING;

/**
 * The default error handling strategy consist of keeping only the error(s) occuring at the farthest
 * error positions, under the assumption that the parse that makes the most progress is the "most
 * correct".
 * <p>
 * This strategy only considers failures to match "error-recording" parsing expression, as well as
 * tokens.
 */
public final class DefaultErrorHandler implements ErrorHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int farthestErrorPosition = -1;

    private Array<ParsingExpression> farthestExpressions = new Array<>(1);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handle(ParsingExpression pe, ParseState state)
    {
        // error recording not set, and not a token instance
        if (((pe.flags & PEF_ERROR_RECORDING) == 0) && !(pe instanceof Token))
        {
            return;
        }

        if (state.start > farthestErrorPosition)
        {
            farthestErrorPosition = state.start;
            farthestExpressions = new Array<>();
        }

        if (state.start == farthestErrorPosition)
        {
            farthestExpressions.add(pe);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParseError error(Source source)
    {
        StringBuilder b = new StringBuilder();

        b.append("The parser failed to match any of the following expressions at position ");
        b.append(source.position(farthestErrorPosition));
        b.append(":\n");

        for (ParsingExpression farthestExpression: farthestExpressions)
        {
            b.append(farthestExpression);
            b.append("\n");
        }

        String message = b.toString();
        return () -> message;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
