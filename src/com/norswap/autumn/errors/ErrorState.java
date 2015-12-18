package com.norswap.autumn.errors;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.source.Source;
import com.norswap.autumn.state.ParseState;

import java.util.Collection;

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

    ErrorChanges changes();

    void merge(ErrorChanges changes);

    void merge(Collection<ErrorLocation> errors);

    void handleError(ParsingExpression pe, ParseState state);

    ErrorReport report(Source source);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
