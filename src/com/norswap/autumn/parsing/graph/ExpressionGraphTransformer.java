package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.graph.slot.Slot;

import java.util.HashMap;
import java.util.Map;

/**
 * An expression graph walker that transforms an expression as it walks it.
 *
 * After each child has been walked, it is replaced by the result of calling {@link #doTransform}
 * with it as the parameter.
 *
 * If {@link #unique} is set, {@link #doTransform} will only be called once on every expression,
 * and the result will be cached, to be reused whenever the expression is encountered again. Note
 * that if you intend to modify the named alternates of an expression cluster, you *must* use
 * this option, otherwise you won't work in filters anymore.
 *
 * If you want to pass the transformation function as a lambda, see {@link FunctionalTransformer}.
 *
 * IMPORTANT NOTE: Never call {@link #doTransform} directly. If you have to perform transformation
 * manually, call {@link #transform} which wraps doTransform in additional logic.
 *
 * You can also extend {@link #afterChild} and {@link #afterRoot} without forgetting to call the
 * super method to add behaviour before/after the transformation.
 */
public abstract class ExpressionGraphTransformer extends ExpressionGraphWalker
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<ParsingExpression, ParsingExpression> transformations;

    public boolean unique = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExpressionGraphTransformer()
    {
        this.transform = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void setup()
    {
        super.setup();

        if (unique) {
            transformations = new HashMap<>();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void teardown()
    {
        super.teardown();
        transformations = null;
    }

    // ---------------------------------------------------------------------------------------------

    protected final ParsingExpression transform(ParsingExpression pe)
    {
        return unique
            ? transformations.computeIfAbsent(pe, this::doTransform)
            : doTransform(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void afterChild(ParsingExpression parent, Slot<ParsingExpression> slot, State state)
    {
        slot.set(doTransform(slot.get()));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void afterRoot(Slot<ParsingExpression> slot)
    {
        slot.set(transform(slot.get()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract ParsingExpression doTransform(ParsingExpression pe);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
