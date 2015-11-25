package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.capture.ParseTreeBuild;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.capture.Decoration;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.annotations.NonNull;
import java.util.Arrays;

public class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public @NonNull Decoration[] decorations;
    public boolean capture;
    public boolean captureText;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Capture(
        boolean capture,
        boolean captureText,
        ParsingExpression operand,
        @NonNull Decoration... decorations)
    {
        this.operand = operand;
        this.capture = capture;
        this.captureText = captureText;
        this.decorations = decorations;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        // save
        ParseTreeBuild oldTree = state.tree;
        int oldCount = state.treeChildrenCount;

        // setup
        ParseTreeBuild newTree = state.tree = new ParseTreeBuild(capture, decorations);
        state.treeChildrenCount = 0;

        // parse
        operand.parse(parser, state);

        // restore
        state.tree = oldTree;
        state.treeChildrenCount = oldCount;

        // add new into old
        if (state.succeeded())
        {
            if (captureText)
            {
                String value = parser.text
                    .subSequence(state.start, state.blackEnd)
                    .toString();
                newTree.value = value;
            }

            if (capture || newTree.childrenCount() != 0)
                oldTree.addChild(newTree);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        return decorations.length == 0
            ? ""
            : Arrays.toString(decorations);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
