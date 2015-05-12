package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing3.Source;

import java.util.ArrayList;
import java.util.Collection;

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

    private Collection<ParseError> farthestErrors = new ArrayList<>(1);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handle(ParsingExpression pe, int fileOffset)
    {
        if (!pe.isErrorRecording() && !pe.isToken())
        {
            return;
        }

        if (fileOffset > farthestErrorPosition)
        {
            farthestErrorPosition = fileOffset;
            farthestErrors = new ArrayList<>(1);
        }

        if (fileOffset >= farthestErrorPosition)
        {
            farthestErrors.add(new ParseError(pe, fileOffset));
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void report(Source source)
    {
        System.err.println("Parse failed at position " + source.position(farthestErrorPosition));
        System.err.println("The parser failed to match any of the following expressions:");

        for (ParseError error: farthestErrors)
        {
            System.err.println(error.failedExpression());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
