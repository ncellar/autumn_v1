package com.norswap.autumn.parsing.expressions.capture;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import java.util.Arrays;

/**
 *
 */
public class Capture extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Decoration[] decorations;
    public boolean capture;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Capture(boolean capture, Decoration... decorations)
    {
        this.decorations = decorations;
        this.capture = capture;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        // save
        // TODO
        BuildParseTree oldTree = null;
        // BuildParseTree oldTree = state.tree;
        int oldCount = state.treeChildrenCount;

        // setup
        // TODO
        BuildParseTree newTree = null;
//        BuildParseTree newTree = state.tree = new BuildParseTree(capture,
//            decorations.length == 0
//                ? null
//                : new Array<>(decorations.clone()));
        state.treeChildrenCount = 0;

        // parse
        operand.parse(parser, state);

        // restore
        // TODO
        state.tree = null;
        // state.tree = oldTree;
        state.treeChildrenCount = oldCount;

        // add new into old
        if (state.succeeded())
            oldTree.addChild(newTree);
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
        return Arrays.toString(decorations);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
