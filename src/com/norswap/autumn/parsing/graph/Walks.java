package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.graph_visit.GraphWalker;
import com.norswap.util.i;
import com.norswap.util.slot.Slot;

import java.util.Arrays;

/**
 *
 */
public class Walks
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static GraphWalker<ParsingExpression> inPlace = (pe, visitor) ->
    {
        i c = i.i();
        Object[] slots = Arrays.stream(pe.children()).map(x -> new ChildSlot(pe, c.i++)).toArray();

        //noinspection Convert2Diamond (don't warn despite IntelliJ bug)
        return new Array<Slot<ParsingExpression>>(slots);
    };

    // ---------------------------------------------------------------------------------------------

    public static GraphWalker<ParsingExpression> copy = (pe, visitor) ->
    {
        i c = i.i();
        ParsingExpression pec = pe.clone();
        Object[] slots = Arrays.stream(pe.children()).map(x -> new ChildSlot(pec, c.i++)).toArray();

        //noinspection Convert2Diamond (don't warn despite IntelliJ bug)
        return new Array<Slot<ParsingExpression>>(slots);
    };

    // ---------------------------------------------------------------------------------------------

    public static GraphWalker<ParsingExpression> readOnly = (pe, visitor) ->
    {
        i c = i.i();
        Object[] slots = Arrays.stream(pe.children()).map(x -> new ChildSlot.ReadOnly(pe, c.i++)).toArray();

        //noinspection Convert2Diamond (don't warn despite IntelliJ bug)
        return new Array<Slot<ParsingExpression>>(slots);
    };

    // ---------------------------------------------------------------------------------------------

    public static GraphWalker<ParsingExpression> inPlaceFirsts(Grammar grammar)
    {
        return (pe, visitor) ->
        {
            i c = i.i();
            Object[] slots = Arrays.stream(pe.firsts(grammar)).map(x -> new ChildSlot(pe, c.i++)).toArray();

            //noinspection Convert2Diamond (don't warn despite IntelliJ bug)
            return new Array<Slot<ParsingExpression>>(slots);
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
