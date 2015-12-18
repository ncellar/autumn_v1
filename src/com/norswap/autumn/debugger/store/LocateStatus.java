package com.norswap.autumn.debugger.store;

public enum LocateStatus
{
    /**
     * Indicates that the current "stack trace" of nodes traversed might be a prefix of the
     * node we seek to locate.
     */
    POSSIBLE_PREFIX,

    /**
     * Indicates that the current "stack trace" of nodes traversed ends with the node we sought
     * to locate.
     */
    MATCH,

    /**
     * Indicates that the current "stack trace" of nodes traversed can't possibly be a prefix of the
     * node we seek to locate.
     */
    DEAD_END,
    ;
}
