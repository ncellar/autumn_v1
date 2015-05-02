package com.norswap.autumn.parsing3;

/**
 *
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

    @Override
    public void memoize(ParseResult result)
    {
        int index = result.position % store.length;

        while (store[index] != null && store[index].position != result.position)
        {
            if (++index == store.length)
            {
                index = 0;
            }
        }

        if (store[index] == null)
        {
            store[index] = result;

            if (++load > store.length * LOAD_FACTOR)
            {
                rehash();
            }
        }
        else
        {
            result.next = store[index];
            store[index] = result;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParseResult get(ParsingExpression pe, int position)
    {
        int index = position % store.length;

        ParseResult output;

        while ((output = store[index]) != null && output.position != position)
        {
            if (++index == store.length)
            {
                index = 0;
            }
        }

        while (output != null && output.expression() == pe)
        {
            output = output.next;
        }

        return output;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void cut(int position)
    {
        // Delete all memoized outputs for positions in [earliestPosition, position[.

        // 1. Delete all outputs in the usual slots. This step suffices if was never any collision
        //    in the store.

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

    // ---------------------------------------------------------------------------------------------

    private void rehash()
    {
        ParseResult[] oldStore = store;
        store = new ParseResult[(int) (store.length * GROWTH_FACTOR)];

        for (ParseResult output: oldStore)
        {
            memoize(output);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
