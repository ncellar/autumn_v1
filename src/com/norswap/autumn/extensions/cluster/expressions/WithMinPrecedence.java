package com.norswap.autumn.extensions.cluster.expressions;

import com.norswap.autumn.extensions.cluster.ClusterExtension;
import com.norswap.autumn.extensions.cluster.ClusterState;
import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;

import static com.norswap.util.Caster.cast;

public final class WithMinPrecedence extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int minPrecedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ClusterState cstate = cast(state.customStates[ClusterExtension.INDEX]);
        ClusterState.Precedence precedence = cstate.getCurrentPrecedence();

        int oldPrecedence = precedence.value;
        precedence.value = minPrecedence;
        operand.parse(parser, state);
        precedence.value = oldPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        return "minPrecedence: " + minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
