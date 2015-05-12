package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing3.Source;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    private CharSequence text;

    private ParserConfiguration configuration;

    private ParseOutput result;

    private int depth = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Parser(Source source, ParserConfiguration configuration)
    {
        this.source = source;
        this.text = source.text();
        this.configuration = configuration;
    }

    public Parser(Source source)
    {
        this(source, ParserConfiguration.DEFAULT);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int parse(ParsingExpression pe)
    {
        ParseFrame rootFrame = ParseFrame.rootFrame(pe);
        result = rootFrame.parseOutput;
        parse(pe, rootFrame);
        rootFrame.parseOutput.setEndPosition(rootFrame.position(), rootFrame.blackPosition(), null);
        return rootFrame.position();
    }

    //----------------------------------------------------------------------------------------------

    public void reportErrors()
    {
        if (result.failed())
        {
            configuration.errorHandler.report(source);
        }
    }

    //----------------------------------------------------------------------------------------------

    public ParseOutput result()
    {
        return result;
    }

    //----------------------------------------------------------------------------------------------

    private void parse(ParsingExpression pe, ParseFrame oldFrame)
    {
        if (configuration.debug)
        {
            if (configuration.debugCountThreshold > 0
                    && ++pe.debugCount > configuration.debugCountThreshold)
            {
                throw new RuntimeException(
                    "Expression " + pe + " has exceeded the count threshold. " +
                    "Ensure it is not stuck in an infinite loop " +
                    "(maybe caused by unmarked left recursion or a loop consuming zero input) " +
                    "or increase/disable the threshold.");
            }

            if (configuration.tracer != null)
            {
                configuration.tracer.accept(pe, depth);
            }
        }

        ++depth;

        // --------------------------------------------------------------------

        if (Precedence.isLower(pe.precedence(), oldFrame.precedence()))
        {
            // We bypass memoization: the expression could be called with a different precedence.
            // We bypass error handling: it is not expected that the input matches this expression.

            oldFrame.fail();
            --depth;
            return;
        }

        // --------------------------------------------------------------------

        // LEFT-RECURSION & MEMO CHECK

        {
            ParseOutput output = null;

            if (pe.isLeftRecursive())
            {
                output = oldFrame.getSeed(pe);

                if (output == null && pe.isLeftAssociative() && oldFrame.isLeftAssociative(pe))
                {
                    // Recursion is blocked in a left-associative expression when not in left
                    // position (if we were in left position, there would have been a seed).

                    // Same comments as for precedence.

                    oldFrame.fail();
                    --depth;
                    return;
                }
            }

            if (output == null && pe.requiresMemoization())
            {
                output = configuration.memoizationStrategy.get(pe, oldFrame.position());
            }

            if (output != null)
            {
                oldFrame.setPositions(output.endPositions());
                oldFrame.mergeOutput(output);

                --depth;
                return;
            }
        }

        // --------------------------------------------------------------------

        // CREATE NEW FRAME TO PASS DOWN

        ParseFrame frame = new ParseFrame(pe, oldFrame);

        // --------------------------------------------------------------------

        // OPERATORS

        ops: while (true) { // goto label for left-recursion

        switch (pe.operator())
        {
            // ------------------------------------

            case OP_SEQUENCE:
            {
                for (ParsingExpression child : pe.operands())
                {
                    parse(child, frame);

                    if (frame.failed())
                    {
                        break;
                    }
                }

            } break;

            // ------------------------------------

            case OP_CHOICE:
            choice: {

                for (ParsingExpression child : pe.operands())
                {
                    parse(child, frame);

                    if (!frame.failed() || frame.isCut())
                    {
                        break choice;
                    }
                    else
                    {
                        frame.resetPositions(oldFrame);
                    }
                }

                frame.fail();

            } break;

            // ------------------------------------

            case OP_LONGEST_MATCH:
            {
                frame.isolateCuts();

                ParseOutput initialParseOutput = frame.parseOutput;
                Positions farthestPosition = new Positions(-1, -1);
                ParseOutput longestOutput = null;

                for (ParsingExpression child : pe.operands())
                {
                    frame.parseOutput = ParseOutput.collect(frame.position());

                    parse(child, frame);

                    if (frame.position() > farthestPosition.position)
                    {
                        farthestPosition = frame.positions();
                        longestOutput = frame.parseOutput;
                    }

                    frame.resetPositionsAndSeeds(oldFrame);
                }

                frame.setPositions(farthestPosition);
                frame.parseOutput = initialParseOutput;

                // No need to reset the seeds.
                // They won't have changed if farthestPosition == 0 and will be empty otherwise.

                frame.mergeOutput(longestOutput);

            } break;

            // ------------------------------------

            case OP_LOOKAHEAD:
            {
                frame.forbidCapture();

                parse(pe.operand(), frame);

                if (!frame.failed())
                {
                    frame.resetPositions(oldFrame);
                }

            } break;

            // ------------------------------------

            case OP_NOT:
            {
                frame.forbidCapture();
                frame.isolateCuts();

                parse(pe.operand(), frame);

                if (frame.failed())
                {
                    frame.resetPositions(oldFrame);
                }
                else
                {
                    frame.fail();
                }

            } break;

            // ------------------------------------

            case OP_OPTIONAL:
            {
                parse(pe.operand(), frame);

                if (frame.failed() && !frame.isCut())
                {
                    frame.resetPositions(oldFrame);
                }

            } break;

            // ------------------------------------

            case OP_ZERO_MORE:
            {
                Positions farthestPositions = frame.positions();

                while (true)
                {
                    parse(pe.operand(), frame);

                    if (frame.failed())
                    {
                        break;
                    }

                    farthestPositions = frame.positions();
                    frame.unCut();
                }

                if (frame.isCut())
                {
                    frame.fail();
                }
                else
                {
                    frame.setPositions(farthestPositions);
                }

            } break;

            // ------------------------------------

            case OP_ONE_MORE:
            {
                parse(pe.operand(), frame);

                if (frame.failed())
                {
                    break;
                }

                Positions farthestPositions = frame.positions();

                while (true)
                {
                    parse(pe.operand(), frame);

                    if (frame.failed())
                    {
                        break;
                    }

                    farthestPositions = frame.positions();
                    frame.unCut();
                }

                if (frame.isCut())
                {
                    frame.fail();
                }
                else
                {
                    frame.setPositions(farthestPositions);
                }

            } break;

            // ------------------------------------

            case OP_LITERAL:
            {
                String string = pe.string();

                int index = 0;
                int pos = frame.position();
                int len = string.length();

                while (index < len && text.charAt(pos) == string.charAt(index))
                {
                    ++index;
                    ++pos;
                }

                if (index != len)
                {
                    frame.fail();
                    break;
                }
                else
                {
                    frame.setPositions(pos);
                }

            } break;

            // ------------------------------------

            case OP_CHAR_RANGE:
            {
                char c = text.charAt(frame.position());

                if (pe.rangeStart() <= c && c <= pe.rangeEnd())
                {
                    frame.setPositions(frame.position() + 1);
                }
                else
                {
                    frame.fail();
                }

                break;
            }
            // ------------------------------------

            case OP_CHAR_SET:
            {
                if (pe.string().indexOf(text.charAt(frame.position())) != -1)
                {
                    frame.setPositions(frame.position() + 1);
                }
                else
                {
                    frame.fail();
                }

            } break;

            // ------------------------------------
            
            case OP_ANY:
            {
                if (text.charAt(frame.position()) != 0)
                {
                    frame.setPositions(frame.position() + 1);
                }
                else
                {
                    frame.fail();
                }

            } break;

            // ------------------------------------

            case OP_CUSTOM:
            {
                pe.customParseFunction().parse(pe, frame);

            } break;

            // ------------------------------------

            case OP_DUMB_CUSTOM:
            {
                frame.setPositions(pe.customDumbParseFunction().parse(pe, frame.position()));

            } break;

            // ------------------------------------

            case OP_CUT:
            {
                if (frame.cut())
                {
                    configuration.memoizationStrategy.cut(frame.position());
                }

            } break;

            // ------------------------------------

            case OP_DUMB:
            {
                frame.setPositions(DumbParser.parse(text, pe.operand(), frame.position()));

            } break;

            // ------------------------------------

            case OP_REF:
            {
                parse(pe.operand(), frame);

            } break;

            // ------------------------------------

            case OP_WHITESPACE:
            {
                parse(configuration.whitespace, frame);

                if (frame.failed())
                {
                    frame.resetPositions(oldFrame);
                }

            } break;
        }

        // --------------------------------------------------------------------

        if (pe.isLeftRecursive())
        {
            ParseOutput seed = oldFrame.popSeed();

            if (seed.endPosition() >= frame.position())
            {
                // In case of either failure or no progress (no left-recursion or left-recursion
                // consuming 0 input), revert to previous seed, which is either successful or
                // returned by ParseOutput#fail().

                frame.setPositions(seed.endPositions());
                frame.parseOutput = seed;
            }
            else
            {
                // Update the seed and retry the rule.

                frame.finalizeOutput(oldFrame, text);
                oldFrame.pushSeed(frame.parseOutput);
                frame.reset(oldFrame);
                continue ops;
            }
        }

        --depth;
        break ops; }

        // --------------------------------------------------------------------

        if (pe.isLeftAssociative())
        {
            frame.popLeftAssociative();
        }

        // --------------------------------------------------------------------

        frame.finalizeOutput(oldFrame, text);

        if (frame.failed())
        {
            configuration.errorHandler.handle(pe, oldFrame.position());
            oldFrame.propagateFailure();
        }
        else
        {
            oldFrame.mergeOutput(frame.parseOutput);

            if (pe.isToken())
            {
                int position = DumbParser.parse(text, configuration.whitespace, frame.position());

                if (position > 0)
                {
                    frame.setPosition(position);
                }
            }

            oldFrame.succeed(frame);
        }

        if (pe.requiresMemoization())
        {
            configuration.memoizationStrategy.memoize(frame.parseOutput);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
