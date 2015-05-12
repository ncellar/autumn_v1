package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing3.Source;

public interface ErrorHandler
{
    /**
     * Indicates that the given expression failed at the given file offset.
     */
    void handle(ParsingExpression expression, int fileOffset);

    /**
     * Record some information if the parse has failed.
     * @param source
     */
    void report(Source source);
}
