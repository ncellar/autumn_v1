package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.test.TestRunner;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

public final class OperatorTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean testDumb = false;

    ParsingExpression pe;

    Runnable[] tests = {
        this::testLiteral,
        this::testAny,
        this::testCharRange,
        this::testCharSet,
        this::testSequence,
        this::testChoice,
        this::testOptional,
        this::testZeroMore,
        this::testOneMore,
        this::testLookahead,
        this::testNot,
        this::testLongestMatch
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        run();
    }

    public static void run()
    {
        new OperatorTests().doRun();
        System.out.println("Operator tests succeeded.");
    }

    void doRun()
    {
        TestRunner runner = new TestRunner(tests);
        runner.run();

        testDumb = true;
        runner.run();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression pe(ParsingExpression pe)
    {
        if (testDumb)
        {
            return dumb(pe);
        }

        return pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void testLiteral()
    {
        Common.ensureMatch(pe(literal("test")), "test");
    }

    // ---------------------------------------------------------------------------------------------

    public void testAny()
    {
        Common.ensureMatch(pe(any()), "x");
    }

    // ---------------------------------------------------------------------------------------------

    public void testCharRange()
    {
        pe = pe(charRange('a', 'c'));
        Common.ensureMatch(pe, "a");
        Common.ensureMatch(pe, "c");
    }

    // ---------------------------------------------------------------------------------------------

    public void testCharSet()
    {
        pe = pe(charSet("abc"));
        Common.ensureMatch(pe, "a");
        Common.ensureMatch(pe, "c");
    }

    // ---------------------------------------------------------------------------------------------

    public void testSequence()
    {
        Common.ensureMatch(pe(sequence(literal("a"), literal("b"), literal("c"))), "abc");
    }

    // ---------------------------------------------------------------------------------------------

    public void testChoice()
    {
        pe = pe(choice(literal("a"), literal("b"), literal("c")));
        Common.ensureMatch(pe, "a");
        Common.ensureMatch(pe, "c");
    }

    // ---------------------------------------------------------------------------------------------

    public void testOptional()
    {
        pe = pe(optional(literal("a")));
        Common.ensureMatch(pe, "a");
        Common.ensureMatch(pe, "");
    }

    // ---------------------------------------------------------------------------------------------

    public void testZeroMore()
    {
        pe = pe(zeroMore(literal("a")));
        Common.ensureMatch(pe, "aaaa");
        Common.ensureMatch(pe, "");
    }

    // ---------------------------------------------------------------------------------------------

    public void testOneMore()
    {
        pe = pe(oneMore(literal("a")));
        Common.ensureMatch(pe, "aaaa");
        Common.ensureFail(pe, "");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLookahead()
    {
        Common.ensureSuccess(pe(lookahead(literal("test"))), "test");
    }

    // ---------------------------------------------------------------------------------------------

    public void testNot()
    {
        pe = pe(not(literal("test")));
        Common.ensureSuccess(pe, "bird");
        Common.ensureFail(pe, "test");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLongestMatch()
    {
        pe = pe(longestMatch(literal("a"), literal("ab"), literal("z"), literal("abc")));
        Common.ensureMatch(pe, "abc");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
