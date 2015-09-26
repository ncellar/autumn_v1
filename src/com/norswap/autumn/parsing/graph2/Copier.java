package com.norswap.autumn.parsing.graph2;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.graph2.NodeState;
import com.norswap.util.graph2.Slot;

import java.util.HashMap;
import java.util.Map;

/**
 * Performs a complete deep copy of a parsing expression.
 */
public final class Copier extends ParsingExpressionVisitor
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<ParsingExpression, ParsingExpression> copies = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(Slot<ParsingExpression> node)
    {
        node.assigned = node.initial.clone();
        node.assigned.copyOwnData();
        copies.put(node.initial, node.assigned);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(Slot<ParsingExpression> parent, Slot<ParsingExpression> child, NodeState state)
    {
        switch (state)
        {
            case FIRST_VISIT:
                parent.assigned.setChild(child.index, child.assigned);
                // avoid retaining the child as a change
                //child.assigned = null;
                break;
            case CUTOFF:
            case VISITED:
                parent.assigned.setChild(child.index, copies.get(child.initial));
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
