package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public final class NextLocator implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ExecutionLocator after;
    public final ExecutionLocator before;
    public final int position;
    public final ParsingExpression pe;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static NextLocatorBuilder builder()
    {
        return new NextLocatorBuilder();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    NextLocator(ExecutionLocator after, ExecutionLocator before, int position, ParsingExpression pe)
    {
        this.after = after;
        this.before = before;
        this.position = position;
        this.pe = pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static class State
    {
        int afterNodeMatchDepth;
        boolean afterNodeMatched = false;
        boolean afterNodeExplored = false;
        boolean beforeNodeMatched = false;
        Object afterState;
        Object beforeState;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public State newState()
    {
        State state = new State();

        if (after != null) {
            state.afterState = after.newState();
        }

        if (before != null) {
            state.beforeState = before.newState();
        }

        return state;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Match match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        State state = (State) locatorState;

        if (after != null && !state.afterNodeExplored)
        {
            if (state.afterNodeMatched)
            {
                if (state.afterNodeMatchDepth <= spine.size())
                {
                    state.afterNodeExplored = true; // !!! only branch that continues
                }
                else {
                    return Match.NONE;
                }
            }
            else
            {
                Match match = after.match(state.afterState, parseState, spine, index, pe);

                if (match == Match.MATCH)
                {
                    state.afterNodeMatched = true;
                    state.afterNodeMatchDepth = spine.size();
                    return Match.NONE;
                }
                else
                {
                    return match; // PREFIX or NONE
                }
            }
        }

        if (before != null)
        {
            if (state.beforeNodeMatched) {
                return Match.OVER;
            }

            Match match = before.match(state.beforeState, parseState, spine, index, pe);

            if (match == Match.MATCH) {
                state.beforeNodeMatched = true;
                return Match.OVER;
            }
        }

        if (position >= 0 && parseState.end - 1 < position)
        {
            return Match.NONE;
        }

        if (this.pe != null && this.pe != pe)
        {
            return Match.PREFIX;
        }

        return Match.MATCH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
