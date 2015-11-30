package com.norswap.autumn.parsing.support.dynext;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.GrammarSyntaxExtension;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;
import java.util.HashMap;
import java.util.HashSet;

/**
 * State for {@link DynExtExtension}.
 * <p>
 * This state has no lifecycle behaviour. It is assumed that extension definitions are never
 * backtracked upon (or if they are, that identical re-definitions will result later) and thet
 * {@link #target} parsing expression will be set then called shortly afterwards.
 */
public class DynExtState implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public HashSet<Extension> extensions = new HashSet<>();

    public HashMap<String, GrammarSyntaxExtension> declSyntaxes = new HashMap<>();

    public HashMap<String, GrammarSyntaxExtension> exprSyntaxes = new HashMap<>();

    public ParsingExpression target;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object extract(ParseState state)
    {
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
