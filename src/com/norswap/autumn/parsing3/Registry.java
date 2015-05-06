package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.FlagFactory;
import com.norswap.autumn.util.HandleFactory;

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

    public static final HandleFactory ParsingExpressionHandleFactory = new HandleFactory();

    public static final FlagFactory ParsingExpressionFlagsFactory = new FlagFactory();

    public static final FlagFactory ParsingInputFlagsFactory = new FlagFactory();

    public static final FlagFactory ParsingOutputFlagsFactory = new FlagFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING EXPRESSION HANDLES (PEH)

    /**
     * Fetches the name of the expression, if it has one.
     */
    public static final int PEH_NAME
        = ParsingExpressionHandleFactory.next();

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
     * (For {@code Capture} only) Indicates the matched text should be captured.
     */
    public static final int PEF_CAPTURE_TEXT
        = ParsingExpressionFlagsFactory.next();

    /**
     * (For {@code Capture} only) Indicates that the capture should be grouped in a result container
     * with other capture of the same name.
     */
    public static final int PEF_CAPTURE_GROUPED
        = ParsingExpressionFlagsFactory.next();

    /**
     * Indicates that all recursions in the sub-expressions of this expression have been resolved.
     */
    public static final int PEF_RESOLVED
        = ParsingExpressionFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING INPUT FLAGS (PIF)

    /**
     * Indicates we should not perform any capture on the sub-expressions of the expression
     * associated with this parse input.
     */
    public static final int PIF_DONT_CAPTURE
        = ParsingInputFlagsFactory.next();

    /**
     * Indicates that the expression associated to this parse input can be cut.
     */
    public static final int PIF_CUTTABLE
        = ParsingInputFlagsFactory.next();

    /**
     * Indicates that we shouldn't memoize any of the sub-expressions of the expression
     * associated with this parse input.
     */
    public static final int PIF_DONT_MEMOIZE
        = ParsingInputFlagsFactory.next();

    /**
     * Indicates that we shouldn't record errors when sub-expressions of the expression
     * associated with this parse input fail to parse.
     */
    public static final int PIF_DONT_RECORD_ERRORS
        = ParsingInputFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING OUTPUT FLAGS (POF)

    /**
     * Indicates that a cut operator has been encountered while parsing the
     * sub-expressions of the expression associated to the parse input owning the output.
     */
    public static final int POF_CUT
        = ParsingOutputFlagsFactory.next();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
