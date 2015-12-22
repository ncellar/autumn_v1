package com.norswap.autumn.extensions.cluster;

import com.norswap.autumn.extensions.CustomStateIndex;
import com.norswap.autumn.extensions.Extension;
import com.norswap.autumn.extensions.SyntaxExtension;
import com.norswap.autumn.extensions.cluster.expressions.ExpressionCluster;
import com.norswap.autumn.extensions.cluster.syntax.SyntaxCluster;
import com.norswap.autumn.extensions.cluster.syntax.SyntaxDrop;
import com.norswap.autumn.extensions.cluster.syntax.SyntaxFilter;
import com.norswap.autumn.state.CustomState;
import java.util.Arrays;

/**
 * Extension that enables using expression clusters in the grammar.
 */
public final class ClusterExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final ClusterState cstate = new ClusterState();
    private SyntaxExtension[] syntaxExtensions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExpressionCluster cluster(ExpressionCluster.Group... groups)
    {
        ExpressionCluster result = new ExpressionCluster(cstate);

        // Sort in decreasing order of precedence.
        Arrays.sort(groups, (g1, g2) -> g2.precedence - g1.precedence);

        result.groups = groups;
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return cstate;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public SyntaxExtension[] syntaxExtensions()
    {
        // TODO pass this to others too
        if (syntaxExtensions == null)
            syntaxExtensions = new SyntaxExtension[] {
                new SyntaxCluster(this),
                new SyntaxFilter(),
                new SyntaxDrop()};

        return syntaxExtensions;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
