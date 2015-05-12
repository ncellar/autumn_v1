package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Trace;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
}
