package com.norswap.autumn.debugger;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.debugger.locators.ExecutionLocator;
import com.norswap.autumn.debugger.store.LocateStatus;
import com.norswap.autumn.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public final class ExecutionLocation implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression[] exprs;
    public final int[] indices;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecutionLocation(ParsingExpression root)
    {
        this.exprs = new ParsingExpression[]{ root };
        this.indices = new int[]{ 0 };
    }

    // ---------------------------------------------------------------------------------------------

    public ExecutionLocation(Array<NodeInfo> spine)
    {
        int len = spine.size() + 1;
        exprs = new ParsingExpression[len];
        indices = new int[len];

        spine.forEach((x, i) ->
        {
            exprs[i] = x.pe;
            indices[i] = x.index;
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public LocateStatus match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        int depth = spine.size();

        return exprs[depth] == pe && indices[depth] == index
            ? exprs.length == depth + 1
                ? LocateStatus.MATCH
                : LocateStatus.POSSIBLE_PREFIX
            : LocateStatus.DEAD_END;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
