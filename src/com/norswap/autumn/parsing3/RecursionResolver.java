package com.norswap.autumn.parsing3;

import com.norswap.autumn.parsing3.expressions.Reference;
import com.norswap.autumn.util.Caster;

import java.util.Arrays;
import java.util.HashSet;

class RecursionResolver
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression recursive;
    private HashSet<ParsingExpression> visited;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    RecursionResolver(ParsingExpression recursive)
    {
        this.recursive = recursive;
        this.visited = new HashSet<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    int walk(ParsingExpression pe)
    {
        if (pe.hasFlagsSet(Registry.PEF_RESOLVED) || visited.contains(pe))
        {
            return 0;
        }

        visited.add(pe);

        int nUnresolved = 0;

        if (pe instanceof Reference && pe.name() != null)
        {
            Reference ref = Caster.cast(pe);
            ParsingExpression[] toProcess = ref.nestedReferences;

            if (recursive.name().equals(pe.name()))
            {
                ref.nestedReferences = null;
                ref.operand = recursive;

                if (toProcess != null)
                {
                    for (ParsingExpression tp: toProcess)
                    {
                        new RecursionResolver(tp).walk(recursive);
                    }
                }
            }
            else
            {
                toProcess = toProcess == null
                    ? new ParsingExpression[1]
                    : Arrays.copyOf(toProcess, toProcess.length + 1);

                toProcess[toProcess.length - 1] = recursive;
                ref.nestedReferences = toProcess;

                ++nUnresolved;
            }
        }
        else
        {
            for (ParsingExpression child: pe.children())
            {
                nUnresolved += walk(child);
            }
        }

        if (nUnresolved == 0)
        {
            pe.setFlags(Registry.PEF_RESOLVED);
        }

        return nUnresolved;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
