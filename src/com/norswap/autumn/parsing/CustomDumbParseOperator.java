package com.norswap.autumn.parsing;

/**
 * Interface to be implemented in order to create a "dumb" custom parsing operator.
 *
 * Dumb custom parsing operators are similar to regular custom parsing operators (see {@link
 * CustomParseOperator}) excepted they don't take a parse frame as parameter and are hence more
 * limited in their capabilities.
 *
 * On the other hand, dumb custom parsing expressions can be called from dumb parsing
 * expressions whereas the regular brand cannot.
 *
 * The return value must be -1 (in case of failure) or greater/equal to `position'.
 */
@FunctionalInterface
public interface CustomDumbParseOperator
{
    int parse(ParsingExpression pe, int position);
}
