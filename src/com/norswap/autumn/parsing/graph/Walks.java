package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.Counter;
import com.norswap.util.slot.Slot;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * This class is a repository of walks over graphs along with modification modes (in-place, copy,
 * read-only).
 */
public class Walks
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Walks the whole graph, modification are done in-place.
     */
    public static GraphWalker<ParsingExpression> inPlace = (slot, visitor) ->
    {
        ParsingExpression pe = slot.get();
        return helper(
            stream(pe.children()),
            (c, x) -> new ChildSlot(pe, c.i++));
    };

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks the whole graph. Attempts to set the value of a slot result in an exception.
     */
    public static GraphWalker<ParsingExpression> readOnly = (slot, visitor) ->
    {
        ParsingExpression pe = slot.get();
        return helper(
            stream(pe.children()),
            (c, x) -> new ReadOnlyChildSlot(pe, c.i++));
    };

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks a graph through the children that are part of the FIRST set of the parent parsing
     * expression. A parsing expression's FIRST set contains all descendant parsing expression that
     * can be invoked at the same input position as the parent, because no input has been consumed
     * yet.
     */
    public static GraphWalker<ParsingExpression> inPlaceFirsts(Grammar grammar)
    {
        return (slot, visitor) ->
        {
            ParsingExpression pe = slot.get();
            return helper(
                stream(pe.firsts(grammar)),
                (c, x) -> new ChildSlot(pe, c.i++));
        };
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * See {@link CopyOnWriteWalker}.
     */
    public static GraphWalker<ParsingExpression> copyOnWriteWalk()
    {
        return new CopyOnWriteWalker();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static Array<Slot<ParsingExpression>> helper(
        Stream<ParsingExpression> stream,
        BiFunction<Counter, ParsingExpression, Slot<ParsingExpression>> f)
    {
        Counter c = new Counter();
        return Array.<Slot<ParsingExpression>>fromUnsafe(stream.map(x -> f.apply(c, x)).toArray());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
