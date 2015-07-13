package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.MultiMap;

import java.util.Set;

/**
 * Computes the FIRST set of parsing expressions.
 *
 * For PEGs, we take the FIRST set of an expression e1 to be any expression e2 that can be entered
 * from e1 without advancing the input.
 *
 * This is not actually in use; I incorrectly assumed it would be necessary for left-recursion
 * detection. Nevertheless it derives pretty easily from {@link ParsingExpression#firsts}, which
 * is necessary for left-recursion detection, and so I leave it standing.
 *
 * We walk our graph and add the result of {@code pe.firsts()} (i.e. the direct children of pe
 * that can be invoked at the same input position) to the FIRST(pe). We then include FIRST(x) in
 * FIRST(pe) for each x we just added (since (FIRST(pe) contains x) => (FIRST(x) subset of FIRST
 * (pe)). But this is not sufficient, due to recursion.
 *
 * To solve the issue, in addition from storing the mapping (x -> y | FIRST(x) contains y), we
 * also store the reverse mapping (x -> y | FIRST(y) contains x). Whenever FIRST(x) is
 * updated, we also update the FIRST set of all expressions in reverse(x).
 *
 * So a FIRST set can change as part of our normal walk, or due to a cascading update via the
 * reverse set. Note that we need to ensure that the set actually changed (i.e. grew) before
 * propagating changes, otherwise we are faced with infinite propagation.
 */
public final class FirstCalculator extends ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The x -> FIRST(x) mapping. Can be freely accessed after the calculator has run.
     */
    public MultiMap<ParsingExpression, ParsingExpression> first;

    private MultiMap<ParsingExpression, ParsingExpression> reverse;

    private Grammar grammar;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public FirstCalculator(Grammar grammar)
    {
        this.grammar = grammar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setup()
    {
        super.setup();
        first = new MultiMap<>();
        reverse = new MultiMap<>();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void teardown()
    {
        super.teardown();
        reverse = null;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void afterAll(ParsingExpression pe)
    {
        ParsingExpression[] peFirsts = pe.firsts(grammar);
        add(pe, peFirsts);

        for (ParsingExpression expr: peFirsts)
        {
            add(pe, first.get(expr));
        }

        Set<ParsingExpression> peNewFirsts = first.get(pe);

        for (ParsingExpression expr: reverse.get(pe))
        {
            addAndPropagate(expr, peNewFirsts);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void addAndPropagate(ParsingExpression pe, Iterable<ParsingExpression> set)
    {
        // The size comparison avoids infinite propagation.
        int size = first.get(pe).size();

        if (add(pe, set).size() > size)
        {
            for (ParsingExpression expr: reverse.get(pe))
            {
                addAndPropagate(pe, set);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void add(ParsingExpression pe, ParsingExpression[] firsts)
    {
        first.addAll(pe, firsts);

        for (ParsingExpression expr: firsts)
        {
            reverse.add(expr, pe);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private Set<ParsingExpression> add(ParsingExpression pe, Iterable<ParsingExpression> firsts)
    {
        Set<ParsingExpression> out = first.addAll(pe, firsts);

        for (ParsingExpression expr: firsts)
        {
            reverse.add(expr, pe);
        }

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
