package com.norswap.autumn.parsing3;

public interface ErrorHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates that the given expression failed at the given file offset.
     */
    void handle(ParsingExpression expression, int fileOffset);

    // ---------------------------------------------------------------------------------------------

    void reportErrors(Parser parser);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
