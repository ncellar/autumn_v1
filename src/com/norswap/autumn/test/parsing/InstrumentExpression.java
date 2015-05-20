package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.StackTrace;
import com.norswap.autumn.parsing.expressions.Trace;
import com.norswap.autumn.util.Array;

import java.util.HashSet;

public final class InstrumentExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashSet<ParsingExpression> visited = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Trace trace(ParsingExpression pe)
    {
        return new InstrumentExpression().doTrace(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public Trace doTrace(ParsingExpression pe)
    {
        if (!visited.contains(pe))
        {
            visited.add(pe);

            ParsingExpression[] children = pe.children();

            for (int i = 0; i < children.length; ++i)
            {
                pe.setChild(i, doTrace(children[i]));
            }
        }

        Trace result = new Trace();
        result.operand = pe;
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static StackTrace stackTrace(ParsingExpression pe)
    {
        return new InstrumentExpression().doStackTrace(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public StackTrace doStackTrace(ParsingExpression pe)
    {
        if (!visited.contains(pe) && !(pe instanceof StackTrace))
        {
            visited.add(pe);

            ParsingExpression[] children = pe.children();

            for (int i = 0; i < children.length; ++i)
            {
                pe.setChild(i, doStackTrace(children[i]));
            }
        }

        StackTrace result = new StackTrace();
        result.operand = pe;
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
