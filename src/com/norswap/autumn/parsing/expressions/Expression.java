package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

import java.util.Arrays;

public class Expression extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class PrecedenceEntry
    {
        public Expression expression;
        public int initialPosition;
        public int minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Operand
    {
        public static Operand regular(ParsingExpression operand, int precedence)
        {
            Operand out = new Operand();
            out.operand = operand;
            out.precedence = precedence;
            return out;
        }

        public static Operand leftRecursive(ParsingExpression operand, int precedence)
        {
            Operand out = new Operand();
            out.operand = operand;
            out.precedence = precedence;
            out.leftRecursive = true;
            return out;
        }

        public static Operand leftAssociative(ParsingExpression operand, int precedence)
        {
            Operand out = new Operand();
            out.operand = operand;
            out.precedence = precedence;
            out.leftRecursive = true;
            out.leftAssociative = true;
            return out;
        }

        public ParsingExpression operand;
        public int precedence;
        public boolean leftRecursive;
        public boolean leftAssociative;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class DropPrecedence extends ParsingExpression
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
     * Each sub-array holds alternates of similar precedence. The array is sorted in order of
     * decreasing precedence.
     */
    public Operand[][] groups;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        // NOTE(norswap): Expression can't contain left-recursive sub-expressions that go through
        // the expression to achieve left-recursion. Neither can the Expression recurse through
        // other Expressions. Formally, any recursion cycle that the Expression is a part of
        // can't contain any LeftRecursive or other Expression nodes.

        OutputChanges changes = input.getSeedChanges(this);

        if (changes != null)
        {
            changes.mergeInto(input);
            return;
        }

        changes = OutputChanges.failure();
        input.pushSheed(this, changes);

        // If we're in a left-recursive position, relying on memoized values will prevent
        // the expansion of the seed, so don't do it. This is cleared when advancing input position
        // with {@link ParseInput#advance()}.

        int oldFlags = input.flags;
        input.forbidMemoization();

        int minPrecedence = parser.enterPrecedence(this, input.start);
        boolean report = true;

        // Try the alternates in decreasing order of precedence, memoizing the previous results.

        for (Operand[] group: groups)
        {
            int groupPrecedence = group[0].precedence;

            if (groupPrecedence < minPrecedence)
            {
                // Block recursing into alternates of lower precedence.
                // If the current alternate is left-associative, also block its own precedence level
                // to avoid non-left recursion (if we were in left position, there would have
                // been a seed).

                // Block non-left-recursion for left-associative alternates.
                // (If we were in left position, there would have been a seed.)

                if (changes.failed())
                {
                    // Bypass error handling; it's unfair to say that the whole expression
                    // failed at this position, because maybe a lower precedence operator would
                    // have matched (e.g. prefix operator with higher precedence than a postfix
                    // operator).

                    report = false;
                }

                break;
            }

            parser.setMinPrecedence(
                groupPrecedence + (group[0].leftAssociative ? 1 : 0));

            Operand[] recursives = Arrays.stream(group)
                .filter(op -> op.leftRecursive)
                .toArray(Operand[]::new);

            for (Operand operand: group)
            {
                operand.operand.parse(parser, input);
                changes = input.popSeed();

                if (changes.end >= input.end)
                {
                    // In case of either failure or no progress (no left-recursion or left-recursion
                    // consuming 0 input), revert to the previous seed.

                    input.pushSheed(this, changes);
                    input.resetAllOutput();
                    input.forbidMemoization();
                }
                else
                {
                    changes = new OutputChanges(input);
                    input.pushSheed(this, changes);
                    input.resetAllOutput();
                    input.forbidMemoization();
                    break;
                }
            }

            if (changes.succeeded())
            while (true)
            {
                OutputChanges oldChanges = changes;

                for (Operand operand: recursives)
                {
                    operand.operand.parse(parser, input);
                    changes = input.popSeed();

                    if (changes.end >= input.end)
                    {
                        // In case of either failure or no progress (no left-recursion or left-recursion
                        // consuming 0 input), revert to the previous seed.

                        input.pushSheed(this, changes);
                        input.resetAllOutput();
                        input.forbidMemoization();
                    }
                    else
                    {
                        changes = new OutputChanges(input);
                        input.pushSheed(this, changes);
                        input.resetAllOutput();
                        input.forbidMemoization();
                        break;
                    }
                }

                if (oldChanges.end >= changes.end)
                {
                    changes = oldChanges;
                    break;
                }
            }
        }

        changes.mergeInto(input);
        input.flags = oldFlags;
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
