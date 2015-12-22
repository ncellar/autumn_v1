package com.norswap.util.graph;

import com.norswap.util.Array;
import com.norswap.util.Int;

import java.util.Collection;
import java.util.HashMap;

import static com.norswap.util.graph.NodeState.*;

/**
 * A graph visit defines a visit of a graph made of nodes with type {@code Node}.
 * <p>
 * The visit is defined via a set of callbacks invoked before/after visiting each node or edge in
 * the graph.at various point of the visit, namely {@link #beforeNode}, {@link #beforeEdge} {@link
 * #afterEdge}, {@link #afterNode}, {@link #afterRoot}. {@link #conclude} is called at the end of
 * the visit.
 * <p>
 * Each node and edge is visited only once, when it is first encountered. {@link #beforeNode} is
 * called upon visiting a new edge. Then, for its first outgoing edge, {@link #beforeEdge} is
 * called. Then the node at the other side of the edge is visited, if it hasn't been already, thus
 * starting this process recursively. When the visit of the other node is complete (or if the node
 * had been visited already), {@link #afterEdge} is called. This process is repeated for all other
 * outgoing edges. Finally {@link #afterNode} is called. The visitor automatically prevents infinite
 * recursion due to cycles.
 * <p>
 * Instead of passing the nodes directly, we pass a {@link Slot} object. These make available the
 * original value of the node ({@link Slot#initial}), and make it possible to indicate that we wish
 * to replace the node with another (by assigning the {@link Slot#assigned} slot. These changes will
 * not be applied until the end of the walk, and their semantics is determined by the implementation
 * of the {@link #applyChanges} method.
 * <p>
 * The visit is started by calling {@link #visit(Node)} (single root) or {@link #visit(Collection)}
 * (multiple roots). It is also possible to perform incremental visits by repeatedly calling the
 * {@link #partialVisit} methods. In the case of an incremental visit, {@link #conclude} must be
 * called manually.
 * <p>
 * Implementation of this class must override the {@link #children(Node))} method, which is
 * responsible to indicate what the children of the passed node are. You can play with this method
 * to change the behaviour of the walk (e.g. only walk over nodes of interest).
 * <p>
 * Visitors are meant to be single-use: they can maintain state within the instance. To perform
 * another visit, create another instance.
 */
public abstract class GraphVisitor<Node>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<Node, NodeState> states = new HashMap<>();

    private Array<Slot<Node>> modified = new Array<>();

    private boolean cutoff;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called first when visiting a node.
     */
    public void beforeNode(Slot<Node> node) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called before visiting the [start -> end] edge. {@code state} is the state of the of end
     * node.
     */
    public void beforeEdge(Slot<Node> start, Slot<Node> end, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after visiting the [start -> end] edge (this includes visiting the end node if it
     * hadn't been already). {@code state} is the state of the of end node.
     */
    public void afterEdge(Slot<Node> start, Slot<Node> end, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after visiting a node, which includes visiting all its outgoing edges and the nodes
     * at the other side of these edges, recursively.
     */
    public void afterNode(Slot<Node> node, Array<Slot<Node>> children) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after attempting to visit a root node. {@code state} can be {@link
     * NodeState#FIRST_VISIT} or {@link NodeState#VISITED}.
     */
    public void afterRoot(Slot<Node> root, NodeState state) {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Called after the walk has concluded (all the roots have been visited). If you are performing
     * an incremental walk, you must call this method yourself. If you override it, you must
     * call the super-method.
     */
    public void conclude()
    {
        states = null;
        applyChanges(modified);
        modified = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Node visit(Node node)
    {
        Slot<Node> out = partialVisit(node);
        conclude();
        return out.latest();
    }

    // ---------------------------------------------------------------------------------------------

    public final Array<Node> visit(Collection<Node> nodes)
    {
        Array<Slot<Node>> out = partialVisit(nodes);
        conclude();
        return out.map(Slot::latest);
    }

    // ---------------------------------------------------------------------------------------------

    public final Slot<Node> partialVisit(Node node)
    {
        Slot<Node> slot = new Slot<>(node);
        partialVisit(slot);
        return slot;
    }

    // ---------------------------------------------------------------------------------------------

    public final Array<Slot<Node>> partialVisit(Collection<Node> nodes)
    {
        Array<Slot<Node>> array = Array.map(nodes, Slot::new);
        array.forEach(this::partialVisit);
        return array;
    }

    // ---------------------------------------------------------------------------------------------

    private void partialVisit(Slot<Node> slot)
    {
        afterRoot(slot, walk(slot));

        if (slot.assigned != null)
            modified.add(slot);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * This can be called from {@link #beforeNode} in order to specify that the children of the node
     * shouldn't be visited.
     */
    public final void cutoff()
    {
        cutoff = true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Call this to mark some nodes as visited, no as not to visit their descendants (at least not
     * through them).
     */
    public final void markVisited(Node node)
    {
        states.put(node, VISITED);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private NodeState walk(Slot<Node> node)
    {
        Node initial = node.initial;

        switch (states.getOrDefault(initial, FIRST_VISIT))
        {
            case FIRST_VISIT:
                states.put(initial, CUTOFF);
                break;

            // Don't enter the node twice.

            case CUTOFF:
                return CUTOFF;

            case VISITED:
                return VISITED;
        }

        beforeNode(node);

        Array<Slot<Node>> children = getChildren(initial);

        for (Slot<Node> child: children)
        {
            NodeState childState;

            childState = states.getOrDefault(child, FIRST_VISIT);
            beforeEdge(node, child, childState);
            childState = walk(child);
            afterEdge(node, child, childState);

            if (child.assigned != null)
            {
                modified.add(child);
            }
        }

        afterNode(node, children);
        states.put(node.initial, VISITED);

        return FIRST_VISIT;
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Array<Slot<Node>> getChildren(Node node)
    {
        if (cutoff)
        {
            cutoff = false;
            return Array.empty();
        }

        Int c = new Int();
        return Array.map(children(node), x -> new Slot<>(x, node, c.i++));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the children of the passed node to visit.
     */
    protected abstract Iterable<Node> children(Node node);

    // ---------------------------------------------------------------------------------------------

    /**
     * Given an array of slots which have been assigned, perform these assignments. For root slots,
     * no further changes is usually necessary (since the client will possess a reference to those
     * slot, he can query them directly).
     */
    protected void applyChanges(Array<Slot<Node>> modified) {}

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
