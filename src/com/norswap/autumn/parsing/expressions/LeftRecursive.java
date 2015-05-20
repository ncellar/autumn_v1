package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.*;
import com.norswap.autumn.util.Array;

public final class LeftRecursive extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean leftAssociative;
    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        OutputChanges changes = input.getSeedChanges(this);

        if (changes != null)
        {
            // There is a seed, use it.

            changes.mergeInto(input);
            return;
        }
        else if (leftAssociative && parser.isLeftAssociative(this))
        {
            // Recursion is blocked in a left-associative expression when not in left
            // position (if we were in left position, there would have been a seed).

            // We bypass error handling: it is not expected that the input matches this expression.

            input.fail();
            return;
        }

        // Push a failed changeset as initial seed.

        if (input.seeds == null)
        {
            input.seeds = new Array<>();
        }

        input.seeds.push(new Seed(this, OutputChanges.failure()));

        if (leftAssociative)
        {
            parser.pushLeftAssociative(this);
        }

        // If we're in a left-recursive position, relying on memoized values will prevent
        // the expansion of the seed, so don't do it. This is cleared when advancing input position
        // with {@link ParseInput#advance()}.
        int oldFlags = input.flags;
        input.forbidMemoization();

        // Keep parsing the operand, as long as the seed keeps growing.

        while (true)
        {
            operand.parse(parser, input);
            OutputChanges oldChanges = input.seeds.pop().changes;
            // TODO remove
            //System.err.println("||" + oldChanges.tree);

            if (oldChanges.end >= input.end)
            {
                // In case of either failure or no progress (no left-recursion or left-recursion
                // consuming 0 input), revert to the previous seed.

                input.flags = oldFlags;
                input.resetAllOutput();
                oldChanges.mergeInto(input);
                break;
            }
            else
            {
                // Update the seed and retry the rule.

                input.seeds.push(new Seed(this, new OutputChanges(input)));
                input.resetAllOutput();
            }
        }

        if (input.failed())
        {
            parser.fail(this, input);
        }

        if (leftAssociative)
        {
            parser.popLeftAssociative();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append(leftAssociative ? "leftAssociative" : "leftRecursive(");
        operand.toString(builder);
        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return new ParsingExpression[]{operand};
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression expr)
    {
        operand = expr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
