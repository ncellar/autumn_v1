package com.norswap.autumn.parsing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.norswap.autumn.parsing.Operator.OP_REF;
import static com.norswap.autumn.parsing.ParsingExpressionFlags.PEF_RESOLVED;


class RecursionResolver
{
    private ParsingExpression ref;
    private Set<ParsingExpression> visited;

    RecursionResolver(ParsingExpression ref)
    {
        this.ref = ref;
        this.visited = new HashSet<>();
    }

    int walk(ParsingExpression pe)
    {
        if (pe.hasFlagsSet(PEF_RESOLVED) || visited.contains(pe))
        {
            return 0;
        }

        visited.add(pe);

        int nUnresolved = 0;

        if (pe.operator() == OP_REF && pe.string() != null)
        {
            if (ref.name().equals(pe.string()))
            {
                ParsingExpression[] toProcess = pe.operands();

                pe.setOperands(null);
                pe.setString(null);
                pe.setOperand(ref);

                if (toProcess != null)
                {
                    for (ParsingExpression tp: toProcess)
                    {
                        new RecursionResolver(tp).walk(ref);
                    }
                }
            }
            else
            {
                ParsingExpression[] toProcess = pe.operands();

                toProcess = toProcess == null
                    ? new ParsingExpression[1]
                    : Arrays.copyOf(toProcess, toProcess.length + 1);

                toProcess[toProcess.length - 1] = ref;
                pe.setOperands(toProcess);

                ++nUnresolved;
            }
        }
        else if (pe.operand() != null)
        {
            nUnresolved += walk(pe.operand());
        }
        else if (pe.operands() != null)
        {
            for (ParsingExpression operand : pe.operands())
            {
                nUnresolved += walk(operand);
            }
        }

        if (nUnresolved == 0)
        {
            pe.setFlags(PEF_RESOLVED);
        }

        return nUnresolved;
    }
}
