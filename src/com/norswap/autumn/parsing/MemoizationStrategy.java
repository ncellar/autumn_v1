package com.norswap.autumn.parsing;

public interface MemoizationStrategy
{
    void memoize(ParseOutput output);

    /**
     * Return a memoized output; or null if no such output has been memoized.
     */
    ParseOutput get(ParsingExpression pe, int position);

    /**
     * Called to indicate that all memoized results between the start of the input and the
     * indicated position will no longer be needed; they can thus be released.
     */
    void cut(int position);
}
