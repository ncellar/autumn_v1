package com.norswap.autumn.parsing;

import com.norswap.util.DeepCopy;

/**
 * The interface to be implemented by user-defined classes to be used as additional input to the
 * parse.
 * <p>
 * One can separate the fields of each parse input into three types.
 * <p>
 * TYPE 1 FIELDS: these fields (potentially) modifies how the sub-expressions are parsed. These
 * fields needs to be included in the key when memoizing.
 * <p>
 * TYPE 2 FIELDS: these fields modify the parse output after the parse is complete, through
 * {@linkTODO} (see TODO).
 * <p>
 * TYPE 3 FIELDS: fields that do not fall into the 1 or 2 categories, but are nevertheless part of
 * the input.
 * <p>
 * Consider a parsing stack (a list of ongoing parsing expression invocations). Assume that for the
 * topmost invocation in the stack, we save a copy of the {@link StandardParseInput} derived from
 * the {@link ParseState} along with copies of all {@link ParseInput} in the parse state, as well as
 * a copy of the {@link Parser}. This data must be sufficient to resume the parse at a later time.
 * This mostly explains the necessity for type 3 fields.
 * <p>
 * A parse input must be deep copyable (but if it is immutable, it can just return itself).
 * <p>
 * Parsing expressions are responsible for the manipulation of parse inputs: modifying them, and
 * adding/removing them from a {@link ParseState}. Many strategies are possible, including
 * repurposing the same ParseInput object to be used many times.
 */
public interface ParseInput extends DeepCopy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes an initial hash code and transforms it to produce another hash code, factoring in
     * only this input's type 1 fields.
     */
    int inputHashCode(int hashCode);

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicate whether this input is equivalent to another, factoring in only this input's type 1
     * fields.
     */
    boolean inputEquals(ParseInput other);

    // ---------------------------------------------------------------------------------------------



    ////////////////////////////////////////////////////////////////////////////////////////////////
}
