package com.norswap.autumn.parsing;

/**
 * Interface to be implemented in order to create a custom parsing operator.
 *
 * We call expressions using a custom operator "custom expressions".
 *
 * Custom expressions must satisfy the following constraints:
 *
 * - The same expression must yield the same parse output each time it is invoked at the same input
 *   position. It is allowable to use global state, but only if it is guaranteed that this state
 *   won't change between two invocations of the expression at the same input position.
 *
 * - The final value of `pf.position` must be -1 (in case of failure) or greater/equal to its
 *   initial value.
 *
 * Note that `pf` is a "new frame", not an "old frame" (see {@link ParseFrame}). As such, it can
 * be modified by the means listed in {@link ParseFrame}.
 */
@FunctionalInterface
public interface CustomParseOperator
{
    void parse(ParsingExpression pe, ParseFrame pf);
}
