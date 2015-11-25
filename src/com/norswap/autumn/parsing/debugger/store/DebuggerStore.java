package com.norswap.autumn.parsing.debugger.store;

import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.CaptureInfo;
import com.norswap.autumn.parsing.debugger.Debugger;
import com.norswap.autumn.parsing.debugger.ExecutionLocation;
import com.norswap.autumn.parsing.debugger.locators.CaptureLocator;
import com.norswap.autumn.parsing.debugger.locators.ExecutionLocator;
import com.norswap.autumn.parsing.debugger.Invocation;
import com.norswap.autumn.parsing.debugger.NodeInfo;
import com.norswap.autumn.parsing.debugger.WindowModel;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseInputs;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

import static com.norswap.util.Caster.cast;

/**
 *
 */
public final class DebuggerStore
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The debugger this store is associated with. Never changes, but can't be final because
     * of circular initialization concerns.
     */
    public Debugger debugger;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA USED BY BOTH STEPS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * If true, we're currently in the location step, where we attempt to set {@link #targetLocation}
     * to the first location matching {@link #targetLocator}. If false, we're in the gather step,
     * where we gather data about the target location.
     */
    boolean locating;

    /**
     * This array is used by both steps. In the locate step this holds a possible prefix of the
     * target location, and will be used to construct that location when found. In the gather step,
     * this is used to gather the {@link ParseInputs} objects corresponding to each node in the
     * location.
     */
    Array<NodeInfo> spine;

    /**
     * This is used in both steps to refer to the index of the node we are currently visiting. The
     * index refers to the order of the node amongst the children of the last node in {@link
     * #spine}.
     */
    int index;

    /**
     * The target location. This will be filled in by the location step and used by the gather step.
     */
    ExecutionLocation targetLocation;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOCATE STEP DATA
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The current status of the locate step.
     */
    LocateStatus lStatus;

    /**
     * An object that tracks the state of {@link #targetLocator}.
     */
    Object locatorState;

    /**
     * A locator that predicates the node whose info we must capture in order to display it
     * in a window.
     */
    ExecutionLocator targetLocator;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GATHER STEP DATA
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The current status of the gather step.
     */
    GatherStatus gStatus;

    /**
     * Data about the invocation of the target node.
     */
    Invocation targetInvocation;

    /**
     * Data about the invocation the children of the target node.
     */
    Array<Invocation> targetChildrenInvocation;

    /**
     * This stack tracks the index associated to each {@link Capture} node encountered under the
     * target node, but not including nodes under a "capturing" ({@link Capture#capture}
     * capture node. The index tracks the order in which these nodes are encountered.
     * <p>
     * The reason this stack is needed is because the encounter order must be determined before
     * processing the capture's children; whereas recording the captures occurs after processing its
     * children.
     */
    Array<Integer> captureIndices;

    /**
     * The counter used to fill {@link #captureIndices}.
     */
    int captureCounter;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new window about the first node matched by the given locator. The node will be
     * reached from the location described by {@code initialSpine} using the given inputs.
     * <p>
     * This proceeds in two steps, leading to two distinct parses. First, in the locate step, a
     * precise {@link ExecutionLocation} is located for the given locator. Second, in the gather
     * step, all the required data is collected.
     */
    public WindowModel windowFor(
        ExecutionLocator locator,
        Array<NodeInfo> initialSpine,
        ParseInputs inputs)
    {
        locating = true;
        spine = initialSpine.clone();
        index = 0;
        lStatus = LocateStatus.POSSIBLE_PREFIX;
        targetLocator = locator;
        locatorState = locator.newState();

        try {
            debugger.parser.parse(inputs);
        }
        catch (HighSpeedUnwindBullet b) { /* SWOOOSH! */ }

        if (targetLocation == null) {
            return null;
        }

        return windowFor(targetLocation, initialSpine, inputs);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a new window displaying info about the node at the given location. The node will
     * be reached from the location described by {@code initialSpine} using the given inputs.
     * <p>
     * Since the location is already known, this only performs the data gathering step.
     */
    public WindowModel windowFor(
        ExecutionLocation location,
        Array<NodeInfo> initialSpine,
        ParseInputs inputs)
    {
        locating = false;
        gStatus = GatherStatus.SPINE;
        spine = initialSpine.clone();
        index = 0;
        targetChildrenInvocation = new Array<>();
        captureCounter = 0;
        captureIndices = new Array<>();

        ParseResult result = null;

        try {
            result = debugger.parser.parse(inputs);
        }
        catch (HighSpeedUnwindBullet b) { /* PEW PEW */ }

        // TODO result might be null!

        Array<CaptureList> captures = cast(result.customChanges.get(X));

        Array<CaptureInfo> captureInfos = new Array<>(captures.size());

        for (CaptureList list: captures)
        {
            Array<CaptureLocator> locators = new Array<>();

            while (list != null)
            {
                locators.add(new CaptureLocator(list.capture, list.index));
                list = list.next;
            }

            captureInfos.add(new CaptureInfo(locators));
        }

        WindowModel out = new WindowModel(
            debugger,
            new ExecutionLocation(spine),
            targetInvocation,
            targetChildrenInvocation,
            spine,
            captureInfos);

        // Don't hold on to unneeded objects.
        targetLocation = null;
        targetLocator = null;
        locatorState = null;
        spine = null;
        targetInvocation = null;
        targetChildrenInvocation = null;
        captureIndices  = null;

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Object before(ParsingExpression pe, ParseState state)
    {
        return locating
            ? beforeLocate(pe, state)
            : beforeGather(pe, state);
    }

    // ---------------------------------------------------------------------------------------------

    public void after(Object penultimate, ParsingExpression pe, ParseState state)
    {
        if (locating)
            afterLocate((LocateStatus) penultimate, pe, state);
        else
            afterGather((GatherStatus) penultimate, pe, state);
    }

    // ---------------------------------------------------------------------------------------------

    public Object beforeLocate(ParsingExpression pe, ParseState state)
    {
        if (lStatus == LocateStatus.DEAD_END) {
            return lStatus;
        }

        Object out = lStatus;
        lStatus = targetLocator.match(locatorState, state, spine, index, pe);

        switch (lStatus)
        {
            case POSSIBLE_PREFIX:
                spine.push(new NodeInfo(pe, index));
                index = 0;
                break;

            case MATCH:
                spine.push(new NodeInfo(pe, index));
                targetLocation = new ExecutionLocation(spine);
                throw new HighSpeedUnwindBullet(); // -> !! <-

            case DEAD_END:
                break;
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public void afterLocate(LocateStatus penultimate, ParsingExpression pe, ParseState state)
    {
        if (targetLocator.promote(lStatus, locatorState, state, spine, index, pe))
        {
            if (lStatus == LocateStatus.DEAD_END) {
                spine.push(new NodeInfo(pe, index));
            }

            throw new HighSpeedUnwindBullet(); // -> !! <-
        }

        switch (lStatus)
        {
            case POSSIBLE_PREFIX:
                index = spine.pop().index + 1;
                break;

            case DEAD_END:
                if (penultimate == LocateStatus.POSSIBLE_PREFIX)
                    ++ index;
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public Object beforeGather(ParsingExpression pe, ParseState state)
    {
        Object out = gStatus;

        switch (gStatus)
        {
            case DEAD_END:
            case CAPTURING:

                gStatus = GatherStatus.DEAD_END;
                break;

            case TARGET:
            case DESCENDANT:

                if (!(pe instanceof Capture)) {
                    gStatus = GatherStatus.DESCENDANT;
                    break;
                }

                captureIndices.push(captureCounter ++);

                gStatus = ((Capture) pe).capture
                    ? GatherStatus.CAPTURING
                    : GatherStatus.ANNOTATING;

                break;

            case SPINE:

                LocateStatus status = targetLocation.match(null, state, spine, index, pe);

                switch (status)
                {
                    case POSSIBLE_PREFIX:
                        spine.push(new NodeInfo(pe, index, state.inputs(pe)));
                        index = 0;
                        gStatus = GatherStatus.SPINE;
                        break;

                    case MATCH:
                        state.errors.requestErrorRecordPoint();
                        spine.push(new NodeInfo(pe, index, state.inputs(pe)));
                        gStatus = GatherStatus.TARGET;
                        break;

                    case DEAD_END:
                        gStatus = GatherStatus.DEAD_END;
                        break;
                }

                break;
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    private GatherStatus checkForCaptures(ParseState state, ParsingExpression pe)
    {
        if (!(pe instanceof Capture)) {
            return GatherStatus.DESCENDANT;
        }

        captureIndices.push(captureCounter ++);

        return ((Capture) pe).capture
            ? GatherStatus.CAPTURING
            : GatherStatus.DESCENDANT;
    }

    // ---------------------------------------------------------------------------------------------

    public void afterGather(GatherStatus penultimate, ParsingExpression pe, ParseState state)
    {
        switch (gStatus)
        {
            case SPINE:
                break;

            case TARGET:
                targetInvocation = invocation(pe, state);
                state.errors.dismissErrorRecordPoint();
                throw new HighSpeedUnwindBullet(); // -> !! <-

            case DEAD_END:
                if (penultimate == GatherStatus.SPINE)
                    ++index;
                break;

            case ANNOTATING:
                annotateCaptures(state, pe);
                break;

            case CAPTURING:
                recordNewCapture(state, pe);
                break;

            case DESCENDANT:
                break;
        }

        switch (penultimate)
        {
            case SPINE:
                // Will only occur for gStatus == DEAD_END.
                // Other cases will be cutoff by a HighSpeedUnwindBullet.
                // So there's no need to pop the spine.
                ++ index;
                break;

            case TARGET:
                targetChildrenInvocation.add(invocation(pe, state));
                break;

            default:
                break;
        }

        gStatus = penultimate;
    }

    // ---------------------------------------------------------------------------------------------

    private void recordNewCapture(ParseState state, ParsingExpression pe)
    {
        CaptureTrackingState cstate = (CaptureTrackingState) state.customStates[X];
        cstate.captures.add(new CaptureList((Capture) pe, captureIndices.pop()));
    }

    // ---------------------------------------------------------------------------------------------

    private void annotateCaptures(ParseState state, ParsingExpression pe)
    {
        CaptureTrackingState cstate = (CaptureTrackingState) state.customStates[X];

        for (int i = cstate.capturesCount; i < cstate.captures.size(); ++i)
        {
            cstate.captures.set(i,
                new CaptureList((Capture) pe, captureIndices.pop(), cstate.captures.get(i)));
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static int X = 0;

    // ---------------------------------------------------------------------------------------------

    private Invocation invocation(ParsingExpression pe, ParseState state)
    {
        ParseInputs inputs = state.inputs(pe);
        ParseChanges changes = state.extract();

        return new Invocation(
            inputs,
            new ParseResult(
                inputs.start() == 0 && state.end == debugger.parser.source.length(),
                state.end >= 0,
                state.end,
                changes.children.get(0).build()[0],
                changes.customChanges,
                state.errors.changes().report(debugger.parser.source)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
