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

    public static final FlagFactory ParseStateFlagsFactory = new FlagFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING EXPRESSION FLAGS (PEF)

    /**
     * Indicates that errors occuring while matching this expression should be recorded for the
     * sake of error reporting. Note that some expressions (such as captures or memos)
     * voluntarily bypass error recording.
     */
    public static final int PEF_ERROR_RECORDING
        = ParsingExpressionFlagsFactory.next();

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
    // PARSE STATE FLAGS (PSF)

    /**
     * Indicates that we shouldn't record errors when sub-expressions of the expression
     * associated with this parse state fail to parse.
     */
    public static final int PSF_DONT_RECORD_ERRORS
        = ParseStateFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
