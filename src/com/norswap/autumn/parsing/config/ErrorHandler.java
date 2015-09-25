package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.ErrorReport;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.ParsingExpression;

/**
 * The error handler is called by the parser whenever a parsing expression fails to match.
 * <p>
 * The role of the error handler is to record information about these failures and extract meaning
 * for them; for instance by determining which failures were meaningful if the parse fails.
 */
public interface ErrorHandler
{
    /**
     * Indicates that the given expression failed with the given state.
     */
    void handle(ParsingExpression pe, ParseState state);

    /**
     * Return error information about the parse.
     * The source object can be used to translate the input positions.
     */
    ErrorReport error(Source source);
}
