package com.norswap.autumn.parsing;

/**
 * Flags indicating attributes of parsing expressions.
 */
public class ParsingExpressionFlags
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static long i = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates that the rule has been given a name.
     */
    public static long PEF_NAMED
        = 1 << i++;

    /**
     * Indicates that an error can be recorded when this expression fails.
     */
    public static long PEF_ERROR_RECORDING
        = 1 << i++;

    /**
     * Indicates that the parse output of the expression should be captured in its parent
     * capture. No other captures with the same name are allowed under the same parent.
     */
    public static long PEF_SINGLE_CAPTURE
        = 1 << i++;

    /**
     * Indicates that the parse output of the expression should be captured as part of an array
     * in its parent parse output. This allows the same expression to be captured multiple times
     * under the same parent.
     */
    public static long PEF_MULTIPLE_CAPTURE
        = 1 << i++;

    /**
     * Indicates the the matched source text should be captured in a new string. Must be used
     * in conjunction with PEF_SINGLE_CAPTURE or PEF_MULTIPLE_CAPTURE.
     */
    public static long PEF_TEXT_CAPTURE
        = 1 << i++;

    /**
     * Indicates that the parse output of the expression should be memoized.
     *
     * A memoization strategy can choose not to memoize an expression marked with this flag;
     * however the reverse isn't true: to be memoized an expression must have this flag.
     *
     * The reason is that we don't allocate a separate ParseOutput object from expressions that
     * don't require memoization or capture.
     */
    public static long PEF_MEMOIZE
        = 1 << i++;

    /**
     * Indicates that the expression represents a choice that can be cut by the cut operator.
     */
    public static long PEF_CUTTABLE
        = 1 << i++;

    /**
     * Indicates that the expression might be left-recursive.
     */
    public static long PEF_LEFT_RECURSIVE
        = 1 << i++;

    /**
     * Indicates that the expression should be parsed left-associatively.
     * Requires PEF_LEFT_RECURSIVE.
     */
    public static long PEF_LEFT_ASSOCIATIVE
        = 1 << i++;

    /**
     * Indicates that this expression is a token. After parsing a token, the parser skips the
     * whitespace, as defined by a parsing expression held by the parser configuration.
     *
     * It is generally expected that two different tokens will not succeed at the same input
     * position, but this is not a hard requirement.
     *
     * The flag can be used as a hint by the memoization and error handle strategies.
     */
    public static long PEF_TOKEN
        = 1 << i++;

    /**
     * Indicates that this expression contains no unresolved recursive references.
     */
    public static long PEF_RESOLVED
        = 1 << i++;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create a new user-defined flag. The system only supports 64 flags, including the flags
     * defined by the system.
     */
    public static long newCustomFlag()
    {
        if (i >= 64)
        {
            throw new RuntimeException("Flag space (64 flags) exceeded.");
        }

        return 1 << i++;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}