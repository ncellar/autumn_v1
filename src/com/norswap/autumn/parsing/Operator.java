package com.norswap.autumn.parsing;

public enum Operator
{
    /**
     * Invokes all its operands sequentially over the input, until one fails. Each operand is
     * invoked at the final input position of the previous one.
     *
     * Succeeds iff all operands succeed.
     *
     * On success, its final input position is that of its last operand.
     */
    OP_SEQUENCE,

    /**
     * Invokes all its operands at its initial input position, until one succeeds.
     *
     * Succeeds iff one operand succeeds.
     *
     * On success, its final input position is that of the operand that succeeded.
     *
     * Mandatory flag(s): PEF_CUTTABLE.
     */
    OP_CHOICE,

    /**
     * Invokes its operand on the input, then resets the input to its initial position.
     *
     * Succeeds iff its operand succeeds.
     *
     * On success, its final input position is its initial input position.
     */
    OP_LOOKAHEAD,

    /**
     * Invokes its operand on the input, then resets the input to its initial position.
     *
     * Succeeds iff its operand succeeds.
     *
     * On success, its final input position is its initial input position.
     */
    OP_NOT,

    /**
     * Invokes its operand on the input.
     *
     * Always succeeds.
     *
     * On success, its final input position is the final input position of it operand if it
     * succeeded, or its initial input position otherwise.
     *
     * Mandatory flag(s): PEF_CUTTABLE.
     */
    OP_OPTIONAL,

    /**
     * Repeatedly invokes its operand over the input, until it fails. Each invocation occurs at
     * the final input position of the previous one.
     *
     * Always succeeds.
     *
     * On success, its final input position is the final input position of the last successful
     * invocation of its operand.
     *
     * Mandatory flag(s): PEF_CUTTABLE.
     */
    OP_ZERO_MORE,

    /**
     * Repeatedly invokes its operand over the input, until it fails.
     *
     * Succeeds if its operand succeeded at least once.
     *
     * On success, its final input position is the final input position of the last successful
     * invocation of its operand.
     *
     * Mandatory flag(s): PEF_CUTTABLE.
     */
    OP_ONE_MORE,

    /**
     * Attempt to match a literal string to the input.
     *
     * Succeeds if the begin of the input matches the string.
     *
     * On success, its final input position is the initial input position + the size of the string.
     *
     * Mandatory flag(s): PEF_TERMINAL.
     */
    OP_LITERAL,

    /**
     * Attempts to match the next input character to a range of characters.
     *
     * Succeeds if the next input character is the range.
     *
     * On success, its final input position is its initial input position + 1.
     *
     * Mandatory flag(s): PEF_TERMINAL.
     */
    OP_CHAR_RANGE,

    /**
     * Attempts to match the next input character to a range of characters.
     *
     * Succeeds if the next input character is the set.
     *
     * On success, its final input position is its initial input position + 1.
     *
     * Mandatory flag(s): PEF_TERMINAL.
     */
    OP_CHAR_SET,

    /**
     * Matches any character.
     *
     * Succeeds if the end of the input has not been reached.
     *
     * On success, its final input position is its initial input position + 1.
     *
     * Mandatory flag(s): PEF_TERMINAL.
     */
    OP_ANY,

    /**
     * Invokes all its operands at its initial input position.
     *
     * Succeeds if at least one of its operands succeeds.
     *
     * On success, its final input position is the largest amongst the final input positions of its
     * successful operands.
     */
    OP_LONGEST_MATCH,

    /**
     * Triggers a cut operation: signify the the alternatives of the latest choice we made
     * must not be tried.
     *
     * Always succeeds.
     *
     * On success, its final input position is its initial input position.
     */
    OP_CUT,

    /**
     * Parses its operand in "dumb" mode, like a traditional non-memoizing PEG parser. In this
     * mode, most features cannot be used, including:
     *
     * - error reporting
     * - memoization & cutting
     * - left-recursion
     * - associativity
     * - precedence
     * - regular custom parse functions (but CustomDumbParseFunction are okay)
     * - captures
     *
     * Dumb mode incurs much less memory and run-time overhead than the regular mode, but does not
     * have any of its advanced features.
     *
     * It is not necessarily more efficient than the regular mode, because a non-optimized
     * expression cannot benefit from the advanced features such as memoization to fix its
     * performance issues.
     *
     * It is most useful to parse fairly basic combinations of terminal expressions, where the
     * overhead could otherwise be consequent.
     *
     * It is not possible for a child of a dumb expression to switch back to regular mode.
     */
    OP_DUMB,

    /**
     * Invokes a {@link CustomParseOperator} at its initial input position.
     *
     * Succeeds if the function succeeds.
     *
     * On success, its final input position is the position returned by the function.
     */
    OP_CUSTOM,

    /**
     * Invokes a {@link CustomDumbParseOperator} at its initial input position.
     *
     * Succeeds if the function succeeds.
     *
     * On success, its final input position is the position returned by the function.
     */
    OP_DUMB_CUSTOM,

    /**
     * A reference to another expression. A reference can be resolved or unresolved. Resolved
     * references have the PEF_RESOLVED flag set, and the referenced expression as operand.
     * Unresolved references have the name of the referenced expression as string. The parsing
     * expression factory methods take care of resolving references.
     *
     * References are necessary because we might wish to selectively apply some flags to an
     * expression depending on the location where it appears (e.g. memoization, capture).
     *
     * A resolved reference simply invokes its operand at its initial input position.
     */
    OP_REF,

    /**
     * Invokes {@link ParserConfiguration#whitespace} at its initial input position.
     *
     * Always succeeds.
     *
     * On success, its final input position is the final input position of the whitespace
     * expression.
     */
    OP_WHITESPACE,

    /**
     * Used when a bogus operator value is needed.
     */
    OP_BOGUS,

    ;
}
