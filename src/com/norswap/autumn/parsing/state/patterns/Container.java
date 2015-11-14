package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomChanges;
import com.norswap.autumn.parsing.state.CustomState;

public class Container<T> implements CustomState.Snapshot, CustomState.Result, CustomChanges
{
    public T content;
    public Container(T content) { this.content = content; }

    @Override
    public Container<T> result(CustomState.Inputs inputs)
    {
        return this;
    }
}
