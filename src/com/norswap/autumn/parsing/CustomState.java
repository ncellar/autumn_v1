package com.norswap.autumn.parsing;

/**
 *
 */
public interface CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     */
    Object snapshot();

    /**
     *
     */
    void restore(Object snapshot);

    /**
     *
     */
    void commit();

    /**
     *
     */
    void discard();

    /**
     *
     */
    Object extract();

    /**
     *
     */
    void merge(Object changes);

    /**
     *
     */
    void uncommit(Object snapshot);

    /**
     *
     */
    Object inputs();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
