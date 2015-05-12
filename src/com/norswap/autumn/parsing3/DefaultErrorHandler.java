package com.norswap.autumn.parsing3;

import com.norswap.autumn.parsing3.expressions.Token;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing3.Registry.PEF_ERROR_RECORDING;

/**
 * The default error handling strategy consist of reporting the error(s) occuring at the farthest
 * error positions, under the assumption that the parse that makes the most progress is the
 * "most correct".
 *
 * This strategy only considers errors that result from a failure to match a parsing expression
 * marked as "error-recording" or as a token.
 */
public class DefaultErrorHandler implements ErrorHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int farthestErrorPosition = -1;

    private Array<ParsingExpression> farthestExpressions = new Array<>(1);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handle(ParsingExpression pe, int fileOffset)
    {
        if (!pe.hasFlagsSet(PEF_ERROR_RECORDING) && !(pe instanceof Token))
        {
            return;
        }

        if (fileOffset > farthestErrorPosition)
        {
            farthestErrorPosition = fileOffset;
            farthestExpressions = new Array<>();
        }

        if (fileOffset >= farthestErrorPosition)
        {
            farthestExpressions.add(pe);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void reportErrors(Parser parser)
    {
        System.err.println("The parser failed to match any of the following expressions at " +
            "position " + parser.source().position(farthestErrorPosition) + ":");

        for (ParsingExpression pe: farthestExpressions)
        {
            System.err.println(pe);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
