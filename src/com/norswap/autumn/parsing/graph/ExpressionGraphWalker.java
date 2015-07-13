package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.slot.ArraySlot;
import com.norswap.util.slot.ImmutableSlot;
import com.norswap.util.slot.ListSlot;
import com.norswap.util.slot.SelfSlot;
import com.norswap.util.slot.Slot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.norswap.autumn.parsing.graph.ExpressionGraphWalker.State.*;

/**
 * Captures the pattern of walking over recursive parsing expression graphs.
 *
 * Upon entering each node, {@link #before} is called. For each of its children, {@link
 * #afterChild is called}. The state passed indicate to this method indicate whether we just
 * recursively walked the child (ABSENT), if the child was already walked in another branch
 * (VISITED) or if this is a recursive visit of the child (VISITING). After all children have
 * been walked, {@link #afterAll} is called.
 *
 * The algorithm never enters a node twice: {@link #before} and {@link #afterAll} are called only
 * once per node, hence recursion is cutoff when we encounter nodes we have already visited.
 */

public abstract class ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CALLBACKS

    protected void before(ParsingExpression pe) {}

    protected void afterChild(ParsingExpression parent, Slot<ParsingExpression> slot, State state) {}

    protected void afterAll(ParsingExpression pe) {}

    protected void afterRoot(Slot<ParsingExpression> slot) {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public enum State { ABSENT, VISITED, VISITING }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<ParsingExpression, State> states;

    protected boolean transform = false;

    private boolean cutoff = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Call this from one of the callbacks to cutoff further recursion. If called from {@link
     * #before}, this will block recursion into the children of the node. If called from {@link
     * #afterChild}, it will block recursion into further siblings of the child node. Calling this
     * from {@link #afterAll} has no effect.
     */
    public void cutoff()
    {
        cutoff = true;
    }

    // ---------------------------------------------------------------------------------------------

    public void setup()
    {
        states = new HashMap<>();
    }

    // ---------------------------------------------------------------------------------------------

    public void teardown()
    {
        states = null;
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression walk(ParsingExpression pe)
    {
        setup();
        Slot<ParsingExpression> slot = partialWalk(pe);
        teardown();
        return slot.get();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks all the expression in the iterable, using the same state for all. This means that
     * expressions reachable from two entries in the iterable will still be entered only once.
     */
    public Collection<ParsingExpression> walk(Collection<ParsingExpression> exprs)
    {
        setup();
        Collection<ParsingExpression> out = partialWalk(exprs);
        teardown();
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Walks all the expression in the array, using the same state for all. This means that
     * expressions reachable from two entries in the array will still be entered only once.
     */
    public ParsingExpression[] walk(ParsingExpression[] exprs)
    {
        setup();
        partialWalk(exprs);
        teardown();
        return exprs;
    }

    // ---------------------------------------------------------------------------------------------

    public Slot<ParsingExpression> partialWalk(ParsingExpression pe)
    {
        _walk(pe);

        Slot<ParsingExpression> slot = transform
            ? new SelfSlot<>(pe)
            : new ImmutableSlot<>(pe);

        afterRoot(slot);

        return slot;
    }

    // ---------------------------------------------------------------------------------------------

    public Collection<ParsingExpression> partialWalk(Collection<ParsingExpression> exprs)
    {
        Array<ParsingExpression> container = transform
            ? new Array<>(new ParsingExpression[exprs.size()])
            : null;

        int i = 0;
        for (ParsingExpression pe: exprs)
        {
            _walk(pe);

            afterRoot(transform
                ? new ListSlot<>(container, i++).set(pe)
                : new ImmutableSlot<>(pe));
        }

        return transform ? container : exprs;
    }

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression[] partialWalk(ParsingExpression[] exprs)
    {
        for (int i = 0; i < exprs.length; ++i)
        {
            _walk(exprs[i]);
            afterRoot(new ArraySlot<>(exprs, i));
        }

        return exprs;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the state to be passed to pe's parent's {@link #afterChild} invocation for pe.
     */
    private State _walk(ParsingExpression pe)
    {
        switch (states.getOrDefault(pe, ABSENT))
        {
            case ABSENT:
                states.put(pe, VISITING);
                break;

            // Don't enter the node twice.

            case VISITING:
                return VISITING;

            case VISITED:
                return VISITED;
        }

        /**/ cleanup: do {

        before(pe);
        if (cutoff) { break cleanup; }

        ParsingExpression[] children = children(pe);

        for (int i = 0; i < children.length; ++i)
        {
            afterChild(pe, new ChildSlot(pe, i), _walk(children[i]));
            if (cutoff) { break cleanup; }
        }

        /**/ } while(false); // end cleanup

        cutoff = false;
        afterAll(pe);
        states.put(pe, VISITED);
        return ABSENT;
    }

    // ---------------------------------------------------------------------------------------------

    protected ParsingExpression[] children(ParsingExpression pe)
    {
        return pe.children();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
