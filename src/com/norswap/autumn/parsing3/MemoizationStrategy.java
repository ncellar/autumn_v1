package com.norswap.autumn.parsing3;

public interface MemoizationStrategy
{
    void memoize(ParseResult result);

    /**
     * Return a memoized result; or null if no such result has been memoized.
     */
    ParseResult get(ParsingExpression pe, int position);

    /**
     * Called to indicate that all memoized results between the start of the input and the
     * indicated position will no longer be needed; they can thus be released.
     */
    void cut(int position);
}
