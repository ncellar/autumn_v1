package com.norswap.autumn.parsing;

public final class DefaultMemoizationStrategy implements MemoizationStrategy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int INITIAL_SIZE = 256;

    private static double LOAD_FACTOR = 0.5;

    private static double GROWTH_FACTOR = 2;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int earliestPosition = 0;

    private int load = 0;

    private ParseOutput[] store = new ParseOutput[INITIAL_SIZE];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void memoize(ParseOutput output)
    {
        int index = output.startPosition() % store.length;

        while (store[index] != null && store[index].startPosition() != output.startPosition())
        {
            if (++index == store.length)
            {
                index = 0;
            }
        }

        if (store[index] == null)
        {
            store[index] = output;

            if (++load > store.length * LOAD_FACTOR)
            {
                rehash();
            }
        }
        else
        {
            output.next = store[index];
            store[index] = output;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParseOutput get(ParsingExpression pe, int position)
    {
        int index = position % store.length;

        ParseOutput output;

        while ((output = store[index]) != null && output.startPosition() != position)
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
            int pos = store[i % store.length].startPosition();

            if (earliestPosition <= pos && pos < position)
            {
                store[i % store.length] = null;
                --load;
            }
        }

        // 2. Delete the outputs that have "ran over" due to open addressing.

        int index = position % store.length;
        ParseOutput output;
        int pos;

        while ((output = store[index]) != null && (pos = output.startPosition()) != position)
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
        ParseOutput[] oldStore = store;
        store = new ParseOutput[(int) (store.length * GROWTH_FACTOR)];

        for (ParseOutput output: oldStore)
        {
            memoize(output);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
