package com.norswap.autumn.parsing.state;

/**
 * Interface implemented by classes that hold custom state to be added to {@link ParseState}. You
 * should read the documentation of {@link ParseState} before going forward.
 * <p>
 * Each method in this interface is called from the method with the equivalent name in {@code
 * ParseState}. Each instance of this class acts as its own mini {@code ParseState} complete with
 * its own snapshot, parse changes and inputs objects (to be defined by the user).
 * <p>
 * It would be tricky to parameterize this interface in terms of the concrete classes used for the
 * snapshot, parse changes and inputs objects. Instead we use three marker interfaces ({@link
 * Changes}, {@link Snapshot} and {@link Inputs}) in the method signatures. You will have to cast
 * these interfaces to the concrete type in the method that accept them as parameter.
 */
public interface CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    interface Changes {}
    interface Snapshot {}
    interface Inputs {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void commit();

    void discard();

    Changes extract();

    void merge(Changes changes);

    Snapshot snapshot();

    void restore(Snapshot snapshot);

    void uncommit(Snapshot snapshot);

    Inputs inputs();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
