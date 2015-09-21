package com.norswap.autumn.parsing;

/**
 * Represent an error output that tracks parsing errors. This parse output is submitted to the same
 * regimen as other parse outputs, but will often act quite different: for instance instead of
 * discarding the output when with {@link #reset} you might wish to save it instead!
 * <p>
 * It's totally possible to save the errors that occur under a successfull expression.
 * <p>
 * The default strategy is implemented by {@link DefaultErrorState}.
 */
public interface ErrorState extends CustomState
{
}
