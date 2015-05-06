package com.norswap.autumn.parsing3;

/**
 * The default memoization strategy memoizes every parse result that it is asked to.
 *
 * It is implement an open-addressing (position -> parse result) map. Multiple results for the same
 * position are linked together using their {@code next} field. Recent results are put at the front
 * of this list.
 */
public final class DefaultMemoizationStrategy implements MemoizationStrategy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int INITIAL_SIZE = 256;
    private static double LOAD_FACTOR = 0.5;
    private static double GROWTH_FACTOR = 2;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int earliestPosition = 0;

    private int load = 0;

    private ParseResult[] store = new ParseResult[INITIAL_SIZE];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int getIndex(int position)
    {
        int index = position % store.length;

        ParseResult result = store[index];

        while (result != null && result.position != position)
        {
            if (++index == store.length)
            {
                index = 0;
            }

            result = store[index];
        }

        return result == null
            ? -index - 1
            : index;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void memoize(ParseResult result)
    {
        if (store.length * LOAD_FACTOR < load + 1)
        {
            grow();
        }

        int index = getIndex(result.position);

        if (index > 0)
        {
            result.next = store[index];
            store[index] = result;
        }
        else
        {
            index = -index - 1;
            store[index] = result;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParseResult get(ParsingExpression pe, int position)
    {
        int index = getIndex(position);

        ParseResult result = index > 0
            ? store[index]
            : null;

        while (result != null && result.expression != pe)
        {
            result = result.next;
        }

        return result;
    }

    // ---------------------------------------------------------------------------------------------

    private void grow()
    {
        ParseResult[] oldStore = store;
        this.store = new ParseResult[(int) (store.length * GROWTH_FACTOR)];

        for (ParseResult output: oldStore)
        {
            memoize(output);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void cut(int position)
    {
        // Delete all memoized outputs for positions in [earliestPosition, position[.

        // 1. Delete all outputs in the usual slots. This step suffices if there was never any
        //    collisions in the store.

        for (int i = earliestPosition; i < position; ++i)
        {
            int pos = store[i % store.length].position;

            if (earliestPosition <= pos && pos < position)
            {
                store[i % store.length] = null;
                --load;
            }
        }

        // 2. Delete the outputs that have "ran over" due to open addressing.
        //    For this we need to scan entries from the insertion point of the position
        //    (assuming no collision) up to the first empty slot.

        int index = position % store.length;
        ParseResult output;
        int pos;

        while ((output = store[index]) != null && (pos = output.position) != position)
        {
            if (earliestPosition <= pos && pos < position)
            {
                store[index] = null;
                --load;
            }

            if (++index == store.length)
            {
                index = 0;
            }
        }

        // 3. Update earliest position.

        earliestPosition = position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
