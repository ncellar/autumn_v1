package com.norswap.autumn.parsing2;

import com.norswap.autumn.parsing.DumbParser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.Precedence;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.util.Caster;

public final class Parser
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Source source;

    private CharSequence text;

    private ParserConfiguration configuration;

    private ParseResult result;

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

    /**
     * Use the parser to match its source text to the given parsing expression.
     *
     * After calling this method, the result of the parse can be retrieved via {@link #result()}.
     *
     * If the result is a failure ({@code result().failed() == true}) or a partial match ({@code
     * matchedWholeSource() == false}), errors can be reported with {@link #reportErrors()}.
     */
    public void parse(ParsingExpression pe)
    {
        ParseInput rootInput = ParseInput.root();
        rootInput.result = result = new ParseResult(pe, 0);
        parse(pe, rootInput);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Report the errors recorded during the parse. The reporting method is up to the {@link
     * ErrorHandler}.
     */
    public void reportErrors()
    {
        configuration.errorHandler.report(source);
    }

    //----------------------------------------------------------------------------------------------

    public ParseResult result()
    {
        return result;
    }

    //----------------------------------------------------------------------------------------------

    public boolean matchedWholeSource()
    {
        return !result.failed() && result.endPosition() == source.length();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A parsing expression is matched to the source text by recursively invoking its sub-expression
     * on the source text in a manner defined by the operator of the expression.
     *
     * A (sub-)expression invocation consists of calling {@link #parse} with the expression as
     * well as some parse input (see {@link ParseInput}). In particular the parse input includes
     * the position in the source text at which to attempt the match.
     *
     * TODO continue
     */

    private void parse(ParsingExpression pe, ParseInput input)
    {
        // --------------------------------------------------------------------

        // In debug mode, check for infinite loops; then run the tracer hook.

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

        final ParseOutput output = input.output;

        // --------------------------------------------------------------------

        if (Precedence.isLower(pe.precedence(), input.precedence))
        {
            // We bypass memoization: the expression could be called with a different precedence.
            // We bypass error handling: it is not expected that the input matches this expression.

            output.fail();
            --depth;
            return;
        }

        // --------------------------------------------------------------------

        // LEFT-RECURSION & MEMO CHECK

        {
            ParseResult result = null;

            if (pe.isLeftRecursive())
            {
                result = input.getSeed(pe);

                if (result == null && input.isLeftAssociative(pe))
                {
                    // Recursion is blocked in a left-associative expression when not in left
                    // position (if we were in left position, there would have been a seed).

                    // Same comments as for precedence.

                    output.fail();
                    --depth;
                    return;
                }
            }

            if (result == null && pe.requiresMemoization())
            {
                // MISMATCH
                result = Caster.cast(configuration.memoizationStrategy.get(pe, input.position));
            }

            if (result != null)
            {
                output.become(result.output);

                if (output.succeeded() && !input.isCaptureForbidden())
                {
                    input.result.add(result);
                }

                --depth;
                return;
            }
        }

        // --------------------------------------------------------------------

        final ParseInput down = new ParseInput(pe, input);
        final ParseOutput up = down.output;

        // --------------------------------------------------------------------

        ops: while (true) {
        switch (pe.operator())
        {
            // ------------------------------------

            case OP_SEQUENCE:
            {
                for (ParsingExpression child : pe.operands())
                {
                    parse(child, down);

                    if (up.succeeded())
                    {
                        down.advance(up);
                    }
                    else
                    {
                        break;
                    }
                }

                output.become(up);
            }
            break;

            // ------------------------------------

            case OP_CHOICE:
            {
                boolean success = false;

                for (ParsingExpression child : pe.operands())
                {
                    parse(child, down);

                    if (up.succeeded())
                    {
                        success = true;
                        output.become(up);
                        break;
                    }
                    else if (up.isCut())
                    {
                        break;
                    }
                }

                if (!success)
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_LONGEST_MATCH:
            {
                down.isolateCuts();

                ParseResult result = down.result;
                ParseOutput farthestOutput = ParseOutput.failure();
                ParseResult longestResult = null;

                for (ParsingExpression child : pe.operands())
                {
                    down.result = ParseResult.container();

                    parse(child, down);

                    if (up.position > farthestOutput.position)
                    {
                        farthestOutput.become(up);
                        longestResult = down.result;
                    }
                }

                output.become(farthestOutput);
                down.result = result;

                if (output.succeeded() && !input.isCaptureForbidden())
                {
                    result.add(longestResult);
                }

            } break;

            // ------------------------------------

            case OP_LOOKAHEAD:
            {
                down.forbidCapture();

                parse(pe.operand(), down);

                if (up.failed())
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_NOT:
            {
                down.forbidCapture();
                down.isolateCuts();

                parse(pe.operand(), down);

                if (up.succeeded())
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_OPTIONAL:
            {
                parse(pe.operand(), down);

                if (up.succeeded() || up.isCut())
                {
                    output.become(up);
                }

            } break;

            // ------------------------------------

            case OP_ZERO_MORE:
            {
                ParseOutput farthestOutput = new ParseOutput(input);

                while (true)
                {
                    parse(pe.operand(), down);

                    if (up.failed())
                    {
                        break;
                    }

                    down.advance(up);
                    farthestOutput.become(up);
                    up.unCut();
                }

                if (up.isCut())
                {
                    output.fail();
                }
                else
                {
                    output.become(farthestOutput);
                }

            } break;

            // ------------------------------------

            case OP_ONE_MORE:
            {
                parse(pe.operand(), down);

                if (up.failed())
                {
                    output.fail();
                    break;
                }

                ParseOutput farthestOutput = new ParseOutput(up);

                while (true)
                {
                    parse(pe.operand(), down);

                    if (up.failed())
                    {
                        break;
                    }

                    down.advance(up);
                    farthestOutput.become(up);
                    up.unCut();
                }

                if (up.isCut())
                {
                    output.fail();
                }
                else
                {
                    output.become(farthestOutput);
                }

            } break;

            // ------------------------------------

            case OP_LITERAL:
            {
                String string = pe.string();

                int index = 0;
                int pos = input.position;
                final int len = string.length();

                while (index < len && text.charAt(pos) == string.charAt(index))
                {
                    ++index;
                    ++pos;
                }

                if (index == len)
                {
                    output.advance(len);
                }
                else
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_CHAR_RANGE:
            {
                char c = text.charAt(input.position);

                if (pe.rangeStart() <= c && c <= pe.rangeEnd())
                {
                    output.advance(1);
                }
                else
                {
                    output.fail();
                }

                break;
            }
            // ------------------------------------

            case OP_CHAR_SET:
            {
                if (pe.string().indexOf(text.charAt(input.position)) != -1)
                {
                    output.advance(1);
                }
                else
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_ANY:
            {
                if (text.charAt(input.position) != 0)
                {
                    output.advance(1);
                }
                else
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_CUSTOM:
            {
                // MISMATCH
                pe.customParseFunction().parse(pe, Caster.cast(down));
                output.become(up);

            } break;

            // ------------------------------------

            case OP_DUMB_CUSTOM:
            {
                int pos = pe.customDumbParseFunction().parse(pe, input.position);

                if (pos > 0)
                {
                    output.advance(pos - input.position);
                }
                else
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_CUT:
            {
                if (output.cut(input))
                {
                    configuration.memoizationStrategy.cut(input.position);
                }

            } break;

            // ------------------------------------

            case OP_DUMB:
            {
                int pos = DumbParser.parse(text, pe.operand(), input.position);

                if (pos > 0)
                {
                    output.advance(pos - input.position);
                }
                else
                {
                    output.fail();
                }

            } break;

            // ------------------------------------

            case OP_REF:
            {
                parse(pe.operand(), down);
                output.become(up);

            } break;


            // ------------------------------------

            case OP_WHITESPACE:
            {
                parse(configuration.whitespace, down);

                if (up.succeeded())
                {
                    output.become(up);
                }

            } break;
        }

        // --------------------------------------------------------------------

        if (pe.isLeftRecursive())
        {
            ParseResult seed = input.seeds.pop();

            if (seed.output().position >= output.position)
            {
                // In case of either failure or no progress (no left-recursion or left-recursion
                // consuming 0 input), revert to the previous seed.

                // It's okay to overwrite {@code input.result} because left-recursive rules never
                // inherit a parse result.

                output.become(seed.output());
                input.result = seed;
            }
            else
            {
                // Update the seed and retry the rule.

                down.result.output.become(output);
                input.seeds.push(down.result);
                down.reset(pe, input);

                continue ops;
            }
        }

        --depth;
        break ops; }

        // --------------------------------------------------------------------

        if (pe.isLeftAssociative())
        {
            input.leftAssociatives.pop();
        }

        // --------------------------------------------------------------------

        if (down.result != input.result)
        {
            down.result.output.become(output);
        }

        if (output.failed())
        {
            configuration.errorHandler.handle(pe, input.position);
            input.result.truncateChildren(input.resultChildrenCount);
        }
        else
        {
            if (input.result != down.result && !input.isCaptureForbidden())
            {
                input.result.add(down.result);
            }

            if (pe.isToken())
            {
                int position = DumbParser.parse(text, configuration.whitespace, output.position);

                if (position > 0)
                {
                    output.position = position;
                }
            }
        }

        if (pe.requiresMemoization())
        {
            // MISMATCH
            configuration.memoizationStrategy.memoize(Caster.cast(down.result));
        }
    }

    //----------------------------------------------------------------------------------------------
}
