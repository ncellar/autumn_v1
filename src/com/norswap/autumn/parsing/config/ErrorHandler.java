package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * The error handler is called by the parser whenever a parsing expression fails to match.
 *
 * The role of the error handler is to record information about these failures and extract meaning
 * for them; for instance by determining which failures were meaningful if the parse fails.
 */
public interface ErrorHandler
{
    /**
     * Indicates that the given expression failed with the given state.
     */
    void handle(ParsingExpression pe, ParseState state);
}
