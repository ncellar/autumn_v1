package com.norswap.autumn.parsing;

public interface MemoizationStrategy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    void memoize(ParsingExpression pe, ParseInput input, OutputChanges changes);

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a memoized result; or null if no such result has been memoized.
     */
    OutputChanges get(ParsingExpression pe, ParseInput input);

    // ---------------------------------------------------------------------------------------------

    /**
     * Called to indicate that all memoized results between the start of the input and the
     * indicated position will no longer be needed; they can thus be released.
     */
    void cut(int position);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
