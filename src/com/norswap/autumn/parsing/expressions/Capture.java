package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.annotations.NonNull;

import static com.norswap.autumn.parsing.Registry.*; // PEF_* PSF_*

/**
 * Invokes its operand on the input, succeeding if the operand does, with the same end position.
 * <p>
 * This either specify the capture of its operand or alters the effect of captures occuring during
 * the invocation of its operand by modifying the parse state.
 * <p>
 * For these captures, the accessor will be {@link #accessor} or {@link ParseState#accessor} (which
 * overrides the former). The tags will be the union of {@link #tags} and {@link ParseState#tags}.
 * If {@link #shouldGroup}, the capture will belong to a group of captures with the same accessor.
 * <p>
 * Capture specifications do not accumulate: after a capture is performed, the {@link
 * ParseState#accessor} and {@link ParseState#tags} are reset for the children of the captured
 * expression.
 * <p>
 * If {@link #shouldCapture}, adds a new child node to the current parse tree node. This node
 * becomes the current parse tree node for the invocation of the operand. If {@link
 * #shouldCaptureText}, the text matching the captured expression will be saved.
 */
public final class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String accessor;
    public Array<String> tags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Capture(ParsingExpression operand, String accessor, @NonNull Array<String> tags, int flags)
    {
        this.operand = operand;
        this.accessor = accessor;
        this.tags = tags;
        this.flags = flags;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        if (shouldCapture())
        {
            ParseTree newTree;

            if (operand == null)
            {
                newTree = new ParseTree();
                state.tree.add(newTree);
            }
            else
            {
                // save
                ParseTree oldTree = state.tree;
                int oldCount = state.treeChildrenCount;

                // setup
                newTree = state.tree = new ParseTree();
                state.treeChildrenCount = 0;

                // parse
                operand.parse(parser, state);

                // restore
                state.tree = oldTree;
                state.treeChildrenCount = oldCount;

                // add new into old
                if (state.succeeded())
                {
                    oldTree.add(newTree);

                    if (shouldCaptureText())
                    {
                        newTree.value = parser.text
                            .subSequence(state.start, state.blackEnd)
                            .toString();
                    }
                }
            }

            annotate(newTree);
        }
        else
        {
            operand.parse(parser, state);

            int start = state.treeChildrenCount;
            int end = state.tree.childrenCount();

            for (int i = start; i < end; ++i)
            {
                annotate(state.tree.child(i));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void annotate(ParseTree tree)
    {
        if (accessor != null)
        {
            if (tree.accessor != null)
            {
                throw new RuntimeException(String.format(
                    "Trying to override accessor \"%s\" with accessor \"%s\".",
                    tree.accessor,
                    accessor));
            }

            tree.accessor = accessor;
        }

        if (shouldGroup())
        {
            tree.group = true;
        }

        for (String tag: tags)
        {
            tree.addTag(tag);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return String.format("accessor: %s, tags: %s, capture: %s",
            accessor, tags,
            shouldCaptureText() ? "text" : shouldGroup() ? "group" : shouldCapture());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] children()
    {
        return operand != null
            ? super.children()
            : new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean shouldCapture()
    {
        return (flags & PEF_CAPTURE) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean shouldCaptureText()
    {
        return (flags & PEF_CAPTURE_TEXT) != 0;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean shouldGroup()
    {
        return (flags & PEF_CAPTURE_GROUPED) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
