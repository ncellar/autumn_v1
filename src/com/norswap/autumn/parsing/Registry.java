package com.norswap.autumn.parsing;

import com.norswap.util.FlagFactory;

/**
 * The registry manages flags and handle spaces for the parser.
 *
 * It registers the standard flags and handles; and allows the user to register his own flags
 * and handles via its factories.
 *
 * This is not synchronized, so take care if concurrent access is required.
 */
public final class Registry
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final FlagFactory ParsingExpressionFlagsFactory = new FlagFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * (For {@code Capture} only) Indicates a capture should be performed.
     */
    public static final int PEF_CAPTURE
        = ParsingExpressionFlagsFactory.next();

    /**
     * (For {@code Capture} only) Indicates the matched text should be captured.
     */
    public static final int PEF_CAPTURE_TEXT
        = ParsingExpressionFlagsFactory.next();

    /**
     * (For {@code Capture} only) Indicates that captures should be added to a group corresponding
     * to their accessor.
     */
    public static final int PEF_CAPTURE_GROUPED
        = ParsingExpressionFlagsFactory.next();

    /**
     * Indicates that all recursions in the sub-expressions of this expression have been resolved.
     */
    public static final int PEF_RESOLVED
        = ParsingExpressionFlagsFactory.next();

    /**
     * Indicates that the parsing expression shouldn't be printed. The parsing expression
     * sporting this flag should only have a single child expression.
     */
    public static final int PEF_UNARY_INVISIBLE
        = ParsingExpressionFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
