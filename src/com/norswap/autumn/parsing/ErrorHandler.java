package com.norswap.autumn.parsing;

public interface ErrorHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates that the given expression failed with the given input.
     */
    void handle(ParsingExpression expression, ParseInput input);

    // ---------------------------------------------------------------------------------------------

    void reportErrors(Parser parser);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
