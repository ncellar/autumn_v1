package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.*;
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
        ParseResult seed = input.getSeed(this);

        if (seed != null)
        {
            // There is a seed, use it.

            input.output.become(seed.output);
            input.merge(seed);
            return;
        }
        else if (leftAssociative && parser.isLeftAssociative(this))
        {
            // Recursion is blocked in a left-associative expression when not in left
            // position (if we were in left position, there would have been a seed).

            // We bypass error handling: it is not expected that the input matches this expression.

            input.output.fail();
            return;
        }

        // Push a failed parse parse result as initial seed.

        if (input.seeds == null)
        {
            input.seeds = new Array<>();
        }

        seed = new ParseResult(this, input.position);
        seed.output = ParseOutput.failure();
        input.seeds.push(seed);

        if (leftAssociative)
        {
            parser.pushLeftAssociative(this);
        }

        // Keep parsing the operand, as long as the seed keeps growing.

        final ParseInput down = new ParseInput(input);
        final ParseOutput up = down.output;

        down.result = new ParseResult(this, input.position);

        // If we're in a left-recursive position, relying on memoized values will prevent
        // the expansion of the seed, so don't do it. This is cleared when advancing input position
        // with {@link ParseInput#advance()}.
        down.forbidMemoization();

        while (true)
        {
            operand.parse(parser, down);
            ParseResult oldSeed = down.seeds.pop();

            if (oldSeed.endPosition() >= up.position)
            {
                // In case of either failure or no progress (no left-recursion or left-recursion
                // consuming 0 input), revert to the previous seed.

                input.load(oldSeed);
                break;
            }
            else
            {
                // Update the seed and retry the rule.

                down.result.finalize(up);
                down.seeds.push(down.result);

                down.resetOutput();
                down.setResult(new ParseResult(this, input.position));
            }
        }

        if (input.output.failed())
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
