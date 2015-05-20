package com.norswap.autumn.parsing;

public interface MemoizationStrategy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    void memoize(ParsingExpression pe, ParseInput input, OutputChanges changes);

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a memoized changeset; or null if no such changeset has been memoized.
     */
    OutputChanges get(ParsingExpression pe, ParseInput input);

    // ---------------------------------------------------------------------------------------------

    /**
     * Called to indicate that all memoized changesets between the start of the input and the
     * indicated position will no longer be needed; they can thus be released.
     */
    void cut(int position);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
