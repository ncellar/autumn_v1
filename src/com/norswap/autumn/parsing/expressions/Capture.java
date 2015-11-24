package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.capture.BuildParseTree;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.capture.decorations.ValueDecoration;
import com.norswap.autumn.parsing.capture.Decoration;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import java.util.Arrays;

public class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Decoration[] decorations;
    public boolean capture;
    public boolean captureText;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Capture(
        boolean capture,
        boolean captureText,
        ParsingExpression operand,
        Decoration... decorations)
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
        BuildParseTree oldTree = state.tree;
        int oldCount = state.treeChildrenCount;

        // setup
        BuildParseTree newTree = state.tree = new BuildParseTree(capture,
            decorations.length == 0
                ? null
                : new Array<>(decorations.clone()));

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
                newTree.addDecoration(new ValueDecoration(value));
            }

            if (capture || newTree.childrenCount() != 0)
                oldTree.addChild(newTree);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void copyOwnData()
    {
        decorations = decorations.clone();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String ownDataString()
    {
        return decorations.length == 0
            ? ""
            : Arrays.toString(decorations);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
