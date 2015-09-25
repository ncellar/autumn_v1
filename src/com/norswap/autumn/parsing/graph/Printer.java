package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.Strings;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.List;
import java.util.function.Consumer;

import static com.norswap.autumn.parsing.ParsingExpressionFlags.PEF_UNARY_INVISIBLE;

/**
 * Prints a parsing expression as a tree.
 * <p>
 * A printer has two options:
 * <p>
 * - cutoffAtNames: indicates that we should stop the recursive descent as soon as named nodes are
 *   encountered.
 * <p>
 * - cutoffAtOwnName: if this is false when cutoffAtNames is true, guarantees that the recursion
 *   won't stop at the root expression itself, but that its direct children (at least) will be
 *   printed as well. Has no effect if cutoffAtNames is false.
 */
public class Printer extends GraphVisitor<ParsingExpression>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Consumer<String> sink;
    private int depth = 0;
    private boolean cutoffAtNames;
    private boolean cutoffAtOwnName;

    private Array<ParsingExpression> stack = new Array<>();
    private Array<Integer> indices = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Printer(Consumer<String> sink, boolean cutoffAtNames, boolean cutoffAtOwnName)
    {
        super(Walks.readOnly);
        this.sink = sink;
        this.cutoffAtNames = cutoffAtNames;
        this.cutoffAtOwnName = cutoffAtOwnName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void before(ParsingExpression pe)
    {
        // PEF_UNARY_INVISIBLE is set
        if ((pe.flags & PEF_UNARY_INVISIBLE) != 0)
        {
            return;
        }

        String name = pe.name;
        String data = pe.ownDataString();

        sink.accept(Strings.times(depth, "-|"));
        sink.accept(pe.getClass().getSimpleName());
        sink.accept(" (");
        sink.accept(nameOrHashCode(pe));
        sink.accept(")");

        if (!data.isEmpty())
        {
            sink.accept(" [" + data + "]");
        }

        if (!stack.isEmpty())
        {
            appendChildData();
        }

        if (name != null && cutoffAtNames && !(depth == 0 && !cutoffAtOwnName))
        {
            cutoff();
        }

        sink.accept("\n");
        stack.push(pe);
        indices.push(0);
        ++ depth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void afterChild(ParsingExpression parent, Slot<ParsingExpression> slot, NodeState state)
    {
        switch (state)
        {
            case CUTOFF:
                sink.accept(Strings.times(depth, "-|"));
                sink.accept("recursive (" + slot.get().name + ")");
                appendChildData();
                sink.accept("\n");
                break;

            case VISITED:
                sink.accept(Strings.times(depth, "-|"));
                sink.accept("visited (");
                sink.accept(nameOrHashCode(slot.get()) + ")");
                appendChildData();
                sink.accept("\n");
                break;

            default:
                break;
        }

        indices.push(indices.pop() + 1);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void after(ParsingExpression pe, List<Slot<ParsingExpression>> children, NodeState state)
    {
        // PEF_UNARY_INVISIBLE is set
        if ((pe.flags & PEF_UNARY_INVISIBLE) != 0)
        {
            stack.pop();
            indices.pop();
            -- depth;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private String nameOrHashCode(ParsingExpression pe)
    {
        String name = pe.name;

        return name != null
            ? name
            : String.format("%X", pe.hashCode());
    }

    // ---------------------------------------------------------------------------------------------

    private void appendChildData()
    {
        String childData = stack.peek().childDataString(indices.peek());

        if (!childData.isEmpty())
        {
            sink.accept(" [" + childData + "]");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
