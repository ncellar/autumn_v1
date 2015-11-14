package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.state.CustomState.Result;
import com.norswap.autumn.parsing.state.errors.ErrorChanges;
import com.norswap.autumn.parsing.state.errors.ErrorReport;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.autumn.parsing.tree.ParseTree;
import com.norswap.util.Array;

import java.util.Collections;

/**
 * [Immutable] The user-facing result of a parse.
 */
public final class ParseResult
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Whether the parse succeeded matching the whole input.
     */
    public final boolean matched;

    /**
     * Whether the parse succeeded matching a prefix (or all) of the input.
     */
    public final boolean succeeded;

    /**
     * The input position where the match ends. Equal to the input size if {@code matched},
     * undefined if {@code !succeeded}.
     */
    public final int endPosition;

    /**
     * The generated parse tree, or null if {@code !succeeded}. If you do not specify any captures
     * in the grammar, this tree is empty when the parse succeeds.
     */
    public final ParseTree tree;

    /**
     * If {@code !matched}, holds error information and diagnostic about the parse.
     * Undefined otherwise.
     */
    public final ErrorReport error;

    /**
     * Custom results generated by the custom states registered by the extensions in use.
     */
    public final Array<Result> customResults;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseResult(
        boolean matched,
        boolean succeeded,
        int endPosition,
        ParseTree tree,
        Array<Result> customResults,
        ErrorReport error)
    {
        this.matched = matched;
        this.succeeded = succeeded;
        this.endPosition = endPosition;
        this.tree = tree;
        this.error = error;
        this.customResults = customResults;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseResult(Source source, ParseInputs inputs, ParseChanges changes, ErrorChanges errorChanges)
    {
        this.matched = inputs.start() == 0 && changes.end == source.length();
        this.succeeded = changes.succeeded();
        this.endPosition = changes.end;

        this.customResults = Array.bimap(
            inputs.customInputs(),
            changes.customChanges,
            (in, out) -> out.result(in));

        Array<BuildParseTree> children = changes.children;

        this.tree = children == null
            ? new BuildParseTree().build()
            : children.size() == 1
                ? children.get(0).build()
                : new ParseTree(
                    null, null, Collections.emptySet(), children.map(BuildParseTree::build));

        this.error = errorChanges.report(source);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
