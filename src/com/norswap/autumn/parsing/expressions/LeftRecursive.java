package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class LeftRecursive extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean leftAssociative;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        OutputChanges changes = state.getSeed(this);

        if (changes != null)
        {
            changes.mergeInto(state);
            return;
        }
        else if (leftAssociative && parser.isBlocked(this))
        {
            // Recursion is blocked in a left-associative expression when not in left
            // position (if we were in left position, there would have been a seed).

            // We bypass error handling: it is not expected that the input matches this expression.

            state.fail();
            return;
        }

        changes = OutputChanges.failure();
        state.pushSheed(this, changes);

        if (leftAssociative)
        {
            parser.pushBlocked(this);
        }

        int oldFlags = state.flags;

        // Keep parsing the operand, as long as long as the seed keeps growing.

        while (true)
        {
            operand.parse(parser, state);

            if (changes.end >= state.end)
            {
                // If no rule could grow the seed, exit the loop.
                break;
            }
            else
            {
                // Update the seed and retry the rule.

                changes = new OutputChanges(state);
                state.setSeed(changes);
                state.discard();
            }
        }

        state.discard();

        state.flags = oldFlags;
        changes.mergeInto(state);
        state.popSeed();

        if (state.failed())
        {
            parser.fail(this, state);
        }

        if (leftAssociative)
        {
            parser.popBlocked();
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
