package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

import java.util.Arrays;

public final class Expression extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class PrecedenceEntry
    {
        public Expression expression;
        public int initialPosition;
        public int minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Operand
    {
        public ParsingExpression operand;
        public int precedence;
        public boolean leftRecursive;
        public boolean leftAssociative;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class DropPrecedence extends ParsingExpression
    {
        public ParsingExpression operand;

        @Override
        public void parse(Parser parser, ParseInput input)
        {
            int minPrecedence = parser.minPrecedence();
            parser.setMinPrecedence(0);
            operand.parse(parser, input);
            parser.setMinPrecedence(minPrecedence);
        }

        @Override
        public void appendTo(StringBuilder builder)
        {
            builder.append("dropPrecedence(");
            operand.toString(builder);
            builder.append(")");
        }

        @Override
        public ParsingExpression[] children()
        {
            return new ParsingExpression[]{operand};
        }

        @Override
        public void setChild(int position, ParsingExpression expr)
        {
            this.operand = expr;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Each sub-array is a group that holds alternates of similar precedence. The array is sorted
      * in order of decreasing precedence.
     */
    public Operand[][] groups;

    /**
     * Each sub-array is a sub-group of the corresponding group in {@link #groups}, holding only
     * the left-recursive operands.
     */
    public Operand[][] recursiveGroups;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        // NOTE(norswap): Expression can't contain left-recursive sub-expressions that go through
        // the expression to achieve left-recursion. Neither can the Expression recurse through
        // other Expressions. Formally, any recursion cycle that the Expression is a part of
        // can't contain any LeftRecursive or other Expression nodes.

        // This variable holds the current seed value throughout the function.
        OutputChanges changes = input.getSeed(this);

        if (changes != null)
        {
            // If this expression is already in the process of being parsed at this position; use
            // the seed value.

            changes.mergeInto(input);
            return;
        }

        changes = OutputChanges.failure();
        input.pushSheed(this, changes);

        // If we're in a left-recursive position, relying on memoized values will prevent
        // the expansion of the seed, so don't do it. This is cleared when advancing input position
        // with {@link ParseInput#advance()}.

        final int oldFlags = input.flags;
        input.forbidMemoization();

        final int minPrecedence = parser.enterPrecedence(this, input.start);

        // Used to sometimes inhibit error reporting.
        boolean report = true;

        for (int i = 0; i < groups.length; ++i)
        {
            Operand[] group = groups[i], recursiveGroup = recursiveGroups[i];

            int groupPrecedence = group[0].precedence;

            // This condition, coupled with the subsequent {@link Parser#setMinPrecedence} call,
            // blocks recursion into alternates of lower precedence. It also blocks recursion into
            // alternates of the same precedence if the current alternate is left-associative; in
            // order to prevent right-recursion (left-recursion is handled via the seed).

            if (groupPrecedence < minPrecedence)
            {
                if (changes.failed())
                {
                    // Bypass error handling: it's unfair to say that the whole expression
                    // failed at this position, because maybe a lower precedence operator would
                    // have matched (e.g. prefix operator with higher precedence than a postfix
                    // operator).

                    report = false;
                }

                // Because alternates are tried in decreasing order of precedence, we can exit
                // the loop immediately.
                break;
            }

            parser.setMinPrecedence(
                groupPrecedence + (group[0].leftAssociative ? 1 : 0));

            while (true)
            {
                OutputChanges oldChanges = changes;

                for (Operand operand: group)
                {
                    operand.operand.parse(parser, input);

                    if (changes.end >= input.end)
                    {
                        // This rule couldn't grow the seed, try the next one.

                        input.resetAllOutput();
                        input.forbidMemoization();
                    }
                    else
                    {
                        // The seed was grown, try to grow it again startin from the first
                        // recursive rule.

                        changes = new OutputChanges(input);
                        input.setSeed(changes);
                        input.resetAllOutput();
                        input.forbidMemoization();
                        break;
                    }
                }

                // If no rule could grow the seed, exit the loop.
                if (oldChanges.end >= changes.end)
                {
                    changes = oldChanges;
                    break;
                }

                // Non-left recursive rules will not yield longer matches, so no use trying them.
                group = recursiveGroup;
            }
        }

        input.flags = oldFlags;
        changes.mergeInto(input);
        input.popSeed();
        parser.exitPrecedence(minPrecedence, input.start);

        if (input.failed() && report)
        {
            parser.fail(this, input);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("expr(");

        for (ParsingExpression operand: children())
        {
            operand.toString(builder);
            builder.append(", ");
        }

        if (groups.length > 0)
        {
            builder.setLength(builder.length() - 2);
        }

        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return Arrays.stream(groups)
            .flatMap(Arrays::stream)
            .map(o -> o.operand)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression expr)
    {
        int pos = 0;

        for (Operand[] group : groups)
        {
            if (position < pos + group.length)
            {
                group[position - pos].operand = expr;
                return;
            }

            pos += group.length;
        }

        throw new RuntimeException(
            "Requesting child " + position + " of an expression with only " + pos + "children.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
