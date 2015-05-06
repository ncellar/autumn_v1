package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing3.Registry.*; // PIF_*

/**
 * <p>Each call to {@link ParsingExpression#parse} takes a parse input as parameter.</p>
 *
 * <p>The parse input indicates:</p>
 *
 * <ul>
 * <li>The position at which to attempt the match.</li>
 * <li>The last non-whitespace position preceding the match position (aka "black position").</li>
 * <li>The current precedence level.</li>
 * <li>
 *   Various flags that influence the parser behaviour (see the PIF section of {@link Registry}).
 * </li>
 * <li>The current seeds for left-recursive expressions starting at the current input position.</li>
 * <li>The innermost parent expression that can be cut.</li>
 * </ul>
 *
 * The parse input also has dedicated output fields:
 *
 * <ul>
 * <li>
 *   An instance of {@link ParseOutput} to indicate the outcome of the parse (success/failure and
 *   input consumed).
 * </li>
 * <li>
 *   An instance of {@link ParseResult} to which the result of captures in sub-expressions should
 *   be attached.
 * </li>
 * </ul>
 *
 * The parse input also keeps track of the number of children of its result instance at creation
 * time. This allows discarding newer captures if we have to backtrack.
 *
 * <hr/>
 *
 * <p>Excepted for the output fields, most fields should have identical content before and after
 * passing the parse input to {@link ParsingExpression#parse}. It is allowable to change the fields
 * during the call, but they need to be restored to their initial values before completing the
 * call.</p>
 *
 * <p>The only exception is {@link #parentCuttable}, which indicates the innermost parent expression
 * that can be cut. Since, by definition, a cut inhibits backtracking, undoing its effects is
 * tricky and generally not needed. One can use {@link #isolateCuts()} in order to prevent a
 * sub-expression from cutting parent expressions. Note that {@link #isolateCuts()} does modify
 * the value of {@link #parentCuttable}.</p>
 *
 * <hr/>
 *
 * <h2>Usage</h2>
 *
 * <p>Each call to {@link ParsingExpression#parse} must ensure that {@link #output} reflects the
 * effect of parsing the expression. {@link #output} is initialized with the same position as the
 * input; so expressions that succeed without consuming any input need not touch anything.</p>
 *
 * <p>The most common ways to set the output are to call {@link Parser#fail} (*), {@link
 * ParseOutput#become} (to reuse the outcome of a sub-expression) or {@link #load} (to reuse the
 * outcome of a saved parse result).</p>
 *
 * <p>(*) {@link Parser#fail} *must* be called in case of failure unless the expression invokes a
 * single sub-expression and inherits its output. This method ensures that captures attached
 * in failed expressions are discarded. It is also necessary to call if errors are to be recorded
 * on the expression.</p>
 *
 * <p>There are two common usage patterns regarding parse inputs in use within {@link
 * ParsingExpression#parse} implementations.</p>
 *
 * <p>The first pattern is to pass the parse input received from the parent directly to the
 * sub-expressions. This is a good choice for expressions with a single sub-expression, or
 * expressions that invoke all their sub-expressions at the same input position (in which case
 * care must be taken to reset the output with {@link #resetOutput} before invoking another
 * expression). You might also find {@link #resetResultChildren} handy.</p>
 *
 * <p>The second pattern is to create a new parse input with the old parse input as parent. This new
 * parse input will mostly inherit the parents fields, but can be modified more freely. This is a
 * good choice for expressions that call their sub-expressions sequentially on the input. In
 * those cases, {@link #advance} is used to update the parse input between sub-expression
 * invocations.</p>
 */
public class ParseInput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int position;
    public int blackPosition;
    public int precedence;

    public int flags;
    public Array<ParseResult> seeds;
    public ParseInput parentCuttable;

    public int resultChildrenCount;

    // output
    public ParseOutput output;
    public ParseResult result;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseInput root()
    {
        return new ParseInput();
    }

    // ---------------------------------------------------------------------------------------------

    private ParseInput()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public ParseInput(ParseInput parent)
    {
        this.position = parent.position;
        this.blackPosition = parent.blackPosition;
        this.precedence = parent.precedence;
        this.seeds = parent.seeds;
        this.flags = parent.flags & (PIF_DONT_CAPTURE | PIF_DONT_MEMOIZE | PIF_DONT_RECORD_ERRORS);

        this.result = parent.result;
        this.resultChildrenCount = result.childrenCount();

        this.output = new ParseOutput(parent);

        this.parentCuttable = parent.isCuttable()
            ? parent
            : parent.parentCuttable;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseResult getSeed(ParsingExpression pe)
    {
        if (seeds == null)
        {
            return null;
        }

        for (ParseResult seed: seeds)
        {
            if (seed.expression == pe)
            {
                return seed;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Advance the position (and related fields) of this parse input up to position pointed to
     * by the passed output. Also updates the fields that records the number of children of the
     * result instance.
     */
    public void advance(ParseOutput up)
    {
        if (up.position > this.position)
        {
            this.seeds = null;
            this.clearFlags(PIF_DONT_MEMOIZE);
        }

        this.position = up.position;
        this.blackPosition = up.blackPosition;
        this.resultChildrenCount = result.childrenCount();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Merge the captures held in the result to this input's result; and updates this input's output
     * to the result's output.
     */
    public void load(ParseResult result)
    {
        output.become(result.output);
        merge(result);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Merge the captures held in the result to this input's result.
     */
    public void merge(ParseResult result)
    {
        if (output.succeeded() && !isCaptureForbidden())
        {
            this.result.add(result);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Set the result instance, and update the field tracking the number of children accordingly.
     */
    public void setResult(ParseResult result)
    {
        this.result = result;
        this.resultChildrenCount = result.childrenCount();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Reset the output field to match the input's position.
     */
    public void resetOutput()
    {
        output.reset(this);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Discard captures in the result that are newer than the time where the field tracking the
     * number of children was last updated (most often at the time the frame was create, although
     * {@link #setResult} and {@link #advance} also modify that field).
     */
    public void resetResultChildren()
    {
        result.children.truncate(resultChildrenCount);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Standard Flags
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void forbidMemoization()
    {
        setFlags(PIF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isMemoizationForbidden()
    {
        return hasFlagsSet(PIF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public void forbidErrorRecording()
    {
        setFlags(PIF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isErrorRecordingForbidden()
    {
        return hasFlagsSet(PIF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public void forbidCapture()
    {
        setFlags(PIF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCaptureForbidden()
    {
        return hasFlagsSet(PIF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prevent cuts on the output or child outputs to cut enclosing choices.
     * This modifies {@link #parentCuttable}.
     */
    public void isolateCuts()
    {
        // This causes {@link ParseOutput#cut()} to always cut the current output and never
        // any of the ancestor cuttable outputs.
        this.parentCuttable = this;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCuttable()
    {
        return hasFlagsSet(PIF_CUTTABLE);
    }

    // ---------------------------------------------------------------------------------------------

    public void setCuttable()
    {
        setFlags(PIF_CUTTABLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Generic Flag Manipulation Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    public boolean hasFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    public void setFlags(int flagsToAdd)
    {
        flags |= flagsToAdd;
    }

    public void clearFlags(int flagsToClear)
    {
        flags &= ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
