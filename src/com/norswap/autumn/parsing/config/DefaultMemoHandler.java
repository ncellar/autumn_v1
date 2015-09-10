package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * The default memoization strategy memoizes every changeset that it is asked to.
 * <p>
 * It is implement as an open-addressing (position -> node) map. Each node includes the position and
 * memoized expression. Multiple nodes changesets for the same position are linked together using
 * their {@code next} field. New changesets are put at the front of this list.
 */
public final class DefaultMemoHandler implements MemoHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int INITIAL_SIZE = 256;
    private static double LOAD_FACTOR = 0.5;
    private static double GROWTH_FACTOR = 2;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int earliestPosition = 0;

    private int load = 0;

    private MemoNode[] store = new MemoNode[INITIAL_SIZE];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final class MemoNode
    {
        int position;
        ParsingExpression pe;
        OutputChanges changes;
        MemoNode next;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int getIndex(int position)
    {
        int index = position % store.length;

        MemoNode node = store[index];

        while (node != null && node.position != position)
        {
            if (++index == store.length)
            {
                index = 0;
            }

            node = store[index];
        }

        return node == null
            ? -index - 1
            : index;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void memoize(ParsingExpression pe, ParseState state, OutputChanges changes)
    {
        MemoNode node = new MemoNode();
        node.position = state.start;
        node.pe = pe;
        node.changes = changes;

        memoize(node);
    }

    // ---------------------------------------------------------------------------------------------

    private void memoize(MemoNode node)
    {
        if (store.length * LOAD_FACTOR < load + 1)
        {
            grow();
        }

        int index = getIndex(node.position);

        if (index >= 0)
        {
            node.next = store[index];
            store[index] = node;
        }
        else
        {
            index = -index - 1;
            store[index] = node;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public OutputChanges get(ParsingExpression pe, ParseState state)
    {
        int index = getIndex(state.start);

        MemoNode node = index >= 0
            ? store[index]
            : null;

        while (node != null && node.pe != pe)
        {
            node = node.next;
        }

        return node == null ? null : node.changes;
    }

    // ---------------------------------------------------------------------------------------------

    private void grow()
    {
        MemoNode[] oldStore = store;
        this.store = new MemoNode[(int) (store.length * GROWTH_FACTOR)];

        for (MemoNode node: oldStore)
        {
            memoize(node);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
