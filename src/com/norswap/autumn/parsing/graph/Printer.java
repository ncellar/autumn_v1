package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Registry;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Strings;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.graph_visit.NodeState;
import com.norswap.util.slot.Slot;

import java.util.List;
import java.util.function.Consumer;

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
        if (pe.hasFlagsSet(Registry.PEF_UNARY_INVISIBLE))
        {
            return;
        }

        String name = pe.name();
        String data = pe.ownDataString();

        sink.accept(Strings.times(depth, "-|"));
        sink.accept(pe.getClass().getSimpleName());
        sink.accept(" (");

        if (name != null)
        {
            sink.accept(name);
        }
        else
        {
            sink.accept(String.format("%X", pe.hashCode()));
        }

        sink.accept(")");

        if (name != null && cutoffAtNames && !(depth == 0 && !cutoffAtOwnName))
        {
            sink.accept("\n");
            cutoff();
            ++ depth;
            return;
        }

        if (!data.isEmpty())
        {
            sink.accept(" [" + data + "]");
        }

        sink.accept("\n");

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
                sink.accept("recursive (" + slot.get().name());
                sink.accept(")\n");
                break;

            case VISITED:
                String name = slot.get().name();
                sink.accept(Strings.times(depth, "-|"));
                sink.accept("visited (");
                sink.accept(name != null
                    ? name
                    : String.format("%X", slot.get().hashCode()));
                sink.accept(")\n");
                break;

            default:
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void after(ParsingExpression parsingExpression, List<Slot<ParsingExpression>> children, NodeState state)
    {
        -- depth;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
