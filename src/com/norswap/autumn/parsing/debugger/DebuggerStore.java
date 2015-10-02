package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.ExecutionLocator.Match;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public final class DebuggerStore
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    Debugger debugger;
    ExecutionLocator target;
    Array<NodeInfo> spine;
    Match last;
    int index;
    Invocation targetInvocation;
    Array<Invocation> targetChildrenInvocation = new Array<>();
    Object locatorState;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void setTarget(ExecutionLocator target, Array<NodeInfo> initialSpine)
    {
        this.target = target;
        this.spine = initialSpine;
        this.last = Match.PREFIX;
        this.index = 0;
        this.targetInvocation = null;
        this.targetChildrenInvocation = new Array<>();
        this.locatorState = target.newState();
    }

    // ---------------------------------------------------------------------------------------------

    Match match(ParsingExpression pe, ParseState parseState)
    {
        switch (last)
        {
            case MATCH:
                return Match.CHILD;

            case PREFIX:
                return target.match(locatorState, parseState, spine, index, pe);

            case NONE:
            case CHILD:
            default:
                return Match.NONE;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public ExecutionLocation location()
    {
        int len = spine.size() + 1;
        ParsingExpression[] exprs = new ParsingExpression[len];
        int[] indices = new int[len];

        spine.forEach((x, i) ->
        {
            exprs[i] = x.pe;
            indices[i] = x.index;
        });

        return new ExecutionLocation(exprs, indices);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
