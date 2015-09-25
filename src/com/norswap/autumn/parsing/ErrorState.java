package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * See {@link ParseState}, section "Error Handling".
 * <p>
 * The default strategy is implemented by {@link DefaultErrorState}.
 */
public interface ErrorState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    void requestErrorRecordPoint();

    void dismissErrorRecordPoint();

    Object changes();

    void merge(Object changes);

    void handleError(ParsingExpression pe, ParseState state);

    ErrorReport report(Source source);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
