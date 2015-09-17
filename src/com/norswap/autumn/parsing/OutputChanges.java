package com.norswap.autumn.parsing;

import com.norswap.util.Array;

/**
 * TODO
 */
public final class OutputChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int end;
    public final int blackEnd;

    private Array<ParseTree> children;

    private Object[] changes;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static OutputChanges failure()
    {
        return new OutputChanges();
    }

    // ---------------------------------------------------------------------------------------------

    private OutputChanges()
    {
        this.end = -1;
        this.blackEnd = -1;
    }

    // ---------------------------------------------------------------------------------------------

    public OutputChanges(ParseState state)
    {
        this.end = state.end;
        this.blackEnd = state.blackEnd;
        this.children = state.tree.children().copyOfSuffix(state.treeChildrenCount);
        //this.children = state.tree.unqualifiedAddedChildren(state);
        this.changes = new Object[state.outputs.length];

        for (int i = 0; i < state.outputs.length; ++i)
        {
            ParseOutput output = state.outputs[i];
            changes[i] = output == null
                ? null
                : output.changes();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void mergeInto(ParseState state)
    {
        state.end = end;
        state.blackEnd = blackEnd;

        if (children != null)
        {
            state.tree.addAll(children);
        }

//        if (children != null)
//        {
//            for (ParseTree child: children)
//            {
//                ParseTree qualified = child.qualify(state);
//                state.tree.add(qualified);
//                state.tree.add(child);
//            }
//        }

        for (int i = 0; i < state.outputs.length; ++i)
        {
            ParseOutput output = state.outputs[i];
            Object change = changes[i];

            if (output != null && change != null)
            {
                output.merge(change);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return end == -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
