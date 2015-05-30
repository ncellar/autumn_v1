package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

public final class LeftRecursive extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean leftAssociative;
    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        OutputChanges changes = input.getSeed(this);

        if (changes != null)
        {
            changes.mergeInto(input);
            return;
        }
        else if (leftAssociative && parser.isBlocked(this))
        {
            // Recursion is blocked in a left-associative expression when not in left
            // position (if we were in left position, there would have been a seed).

            // We bypass error handling: it is not expected that the input matches this expression.

            input.fail();
            return;
        }

        changes = OutputChanges.failure();
        input.pushSheed(this, changes);

        if (leftAssociative)
        {
            parser.pushBlocked(this);
        }

        // If we're in a left-recursive position, relying on memoized values will prevent
        // the expansion of the seed, so don't do it. This is cleared when advancing input position
        // with {@link ParseInput#advance()}.

        int oldFlags = input.flags;
        input.forbidMemoization();

        // Keep parsing the operand, as long as long as the seed keeps growing.

        while (true)
        {
            operand.parse(parser, input);

            if (changes.end >= input.end)
            {
                // If no rule could grow the seed, exit the loop.
                break;
            }
            else
            {
                // Update the seed and retry the rule.

                changes = new OutputChanges(input);
                input.setSeed(changes);
                input.resetAllOutput();
                input.forbidMemoization();
            }
        }

        input.resetAllOutput();
        input.flags = oldFlags;
        changes.mergeInto(input);
        input.popSeed();

        if (input.failed())
        {
            parser.fail(this, input);
        }

        if (leftAssociative)
        {
            parser.popBlocked();
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
