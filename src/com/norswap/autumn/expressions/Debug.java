package com.norswap.autumn.expressions;

import com.norswap.autumn.Parser;
import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.state.ParseState;

/**
 * Use this expression to print out data whenever it is traversed.
 */
public class Debug extends ParsingExpression
{
    public final String id;

    public Debug(String id)
    {
        this.id = id;
    }

    @Override
    public void parse(Parser parser, ParseState state)
    {
        System.err.println(String.format("%s invoked at %s", id, parser.source.position(state.start)));
    }
}
