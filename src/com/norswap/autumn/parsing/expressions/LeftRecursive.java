package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.extensions.BottomupExtension;
import com.norswap.autumn.parsing.state.BottomupState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

import static com.norswap.util.Caster.cast;

public final class LeftRecursive extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean leftAssociative;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        BottomupState bstate = cast(state.customStates[BottomupExtension.INDEX]);
        ParseChanges changes;

        if ((changes = bstate.getSeed(this)) != null)
        {
            // If this is a re-entry, use the seed value.
            state.merge(changes);
            return;
        }
        else if (leftAssociative && bstate.blocked(this))
        {
            // Recursion is blocked in a left-associative expression when not in left
            // position (if we were in left position, there would have been a seed).

            state.fail(this);
            return;
        }

        changes = ParseChanges.failure();
        bstate.setSeed(this, changes, state.start);

        if (leftAssociative)
        {
            bstate.block(this);
        }

        // Keep parsing the operand, as long as long as the seed keeps growing.

        while (true)
        {
            operand.parse(parser, state);

            if (state.end > changes.end)
            {
                // Seed was grown, update it and retry the rule.
                changes = state.extract();
                bstate.setSeed(this, changes, state.start);
                state.discard();
            }
            else
            {
                // No rule could grow the seed, exit the loop.
                state.discard();
                break;
            }
        }

        state.merge(changes);

        bstate.removeSeed(this);

        if (leftAssociative)
        {
            bstate.unblock(this);
        }

        if (state.failed())
        {
            state.fail(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return leftAssociative ? "left-associative" : "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
