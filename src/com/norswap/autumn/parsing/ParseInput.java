package com.norswap.autumn.parsing;

import com.norswap.util.DeepCopy;

/**
 * The interface to be implemented by user-defined classes to be used as additional input to the
 * parse.
 * <p>
 * MODIFIER FIELDS: These fields (potentially) modifies how the sub-expressions are parsed. These
 * fields needs to be included in the key when memoizing. It is possible to have additional fields
 * which are not modifier fields but are nevertheless part of the input.
 * <p>
 * In addition to their use inside parsing expressions, parse input have two roles.
 * <p>
 * First, they can serve as memoization key using their modifier fields (since a difference in a
 * modifier field means the output might be different).
 * <p>
 * Second, they can be used to record the state at any point of the parse, along with an instance of
 * {@link StandardParseInput} and {@link ParseState}. This explains why inputs have non-modifier
 * fields: they are necessary to records data that will be used higher in the call stack.
 * <p>
 * A parse input must be deep copyable (but if it is immutable, it can just return itself).
 * <p>
 * Parsing expressions are responsible for the manipulation of parse inputs: modifying them, and
 * adding/removing them from a {@link ParseState}. Many strategies are possible, including
 * repurposing the same ParseInput object to be used many times.
 *
 * TODO clarifiying
 */
public interface ParseInput extends DeepCopy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes an initial hash code and transforms it to produce another hash code, factoring in
     * only this input's modifier fields.
     */
    int inputHashCode(int hashCode);

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicate whether this input is equivalent to another, factoring in only this input's modifier
     * fields.
     */
    boolean inputEquals(ParseInput other);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
