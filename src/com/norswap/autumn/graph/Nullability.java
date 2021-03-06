package com.norswap.autumn.graph;

import com.norswap.autumn.ParsingExpression;

import java.util.Arrays;

/**
 * A nullability indicates whether a parsing expression is nullable; or, if it can't be determined
 * yet, on what this determination depends on.
 * <p>
 * A nullability can be resolved or unresolved (test with {@link #resolved}; a resolved nullability
 * can be nullable or not (test with {@link #nullable}, but only if the expression is resolved).
 * <p>
 * You can create a resolved nullable with {@link #yes(ParsingExpression)} and test for resolved
 * nullables with {@link #yes()}. You can create a resolved non-nullable with {@link #no
 * (ParsingExpression)} and test for resolved non-nullable with {@link #no()}.
 * <p>
 * Unresolved nullability have an array of expressions to be reduced over ({@link #toReduce}), as
 * well as a reduction function ({@link #reduce}). Abusively, we will call the expressions to be
 * resolved over "the children" of the nullability. The reduction function takes an array of
 * nullability corresponding to the children and tries to return a "reduced" nullability based on
 * the array. In the best case this means returning a resolved nullability. Failing that, the
 * function can diminish the number of children, or leave things unchanged.
 * <p>
 * The idea is that whenever the nullability of a child becomes resolved, the reduction function is
 * run. This can cause the nullability for the expression to become resolved, which will trigger
 * further reductions; etc.
 * <p>
 * Since sometimes a single resolved child nullability suffices to resolve the nullability of its
 * parent, we supply the function {@link #update} as an optimization. It tries to resolve the
 * nullability based on a single child nullability that got resolved. It returns the resolved
 * nullability or null if it failed to resolve based on that, in which case the reduction function
 * then needs to be run to ensure correctness.
 * <p>
 * We suply three default reduction strategies: "all", "any" and "single". The "all" strategy
 * indicates that an expression is nullable if all its children are nullable. The "any" strategy
 * indicates that an expression is nullable of any of its children is nullable. The "single"
 * strategy ties the nullability of an expression to that of its only child.
 */
public class Nullability
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;
    public final boolean resolved;
    public final boolean nullable;
    public final ParsingExpression[] toReduce;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected Nullability(ParsingExpression pe, boolean resolved, boolean nullable, ParsingExpression[] tr)
    {
        this.pe = pe;
        this.resolved = resolved;
        this.nullable = nullable;
        this.toReduce = tr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Nullability reduce(Nullability[] nullabilities)
    {
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public Nullability update(Nullability nullability)
    {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final boolean yes()
    {
        return resolved && nullable;
    }

    // ---------------------------------------------------------------------------------------------

    public final boolean no()
    {
        return resolved && !nullable;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Nullability yes(ParsingExpression pe)
    {
        return new Nullability(pe, true, true, null);
    }

    // ---------------------------------------------------------------------------------------------

    public static Nullability no(ParsingExpression pe)
    {
        return new Nullability(pe, true, false, null);
    }

    // ---------------------------------------------------------------------------------------------

    public static Nullability all(ParsingExpression pe, ParsingExpression[] toReduce)
    {
        return new Nullability(pe, false, false, toReduce)
        {
            @Override
            public Nullability reduce(Nullability[] nullabilities)
            {
                int yeses = 0;

                for (Nullability n : nullabilities)
                    if (n.resolved)
                        if (!n.nullable)
                            return no(pe);
                        else
                            ++ yeses;

                if (yeses == nullabilities.length)
                    return yes(pe);

                return all(pe, Arrays.stream(nullabilities)
                    .filter(n -> !n.resolved)
                    .map(n -> n.pe)
                    .toArray(ParsingExpression[]::new));
            }

            @Override
            public Nullability update(Nullability n)
            {
                return n.no()
                    ? no(pe)
                    : null;
            }
        };
    }

    // ---------------------------------------------------------------------------------------------

    public static Nullability any(ParsingExpression pe, ParsingExpression[] toReduce)
    {
        return new Nullability(pe, false, false, toReduce)
        {
            @Override
            public Nullability reduce(Nullability[] nullabilities)
            {
                int noes = 0;

                for (Nullability n : nullabilities)
                    if (n.resolved)
                        if (n.nullable)
                            return yes(pe);
                        else
                            ++ noes;

                if (noes == nullabilities.length)
                    return no(pe);

                return any(pe, Arrays.stream(nullabilities)
                    .filter(n -> !n.resolved)
                    .map(n -> n.pe)
                    .toArray(ParsingExpression[]::new));
            }

            @Override
            public Nullability update(Nullability n)
            {
                return n.yes()
                    ? yes(pe)
                    : null;
            }
        };
    }

    // ---------------------------------------------------------------------------------------------

    public static Nullability single(ParsingExpression pe, ParsingExpression operand)
    {
        return new Nullability(pe, false, false, new ParsingExpression[]{operand})
        {
            @Override
            public Nullability reduce(Nullability[] nullabilities)
            {
                return nullabilities[0].resolved
                    ? nullabilities[0].nullable
                        ? yes(pe)
                        : no(pe)
                    : this;
            }

            @Override
            public Nullability update(Nullability n)
            {
                return n.resolved
                    ? n.nullable
                        ? yes(pe)
                        : no(pe)
                    : null;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}