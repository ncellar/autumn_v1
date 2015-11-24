package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.*;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.capture.Decoration;
import com.norswap.autumn.parsing.capture.decorations.*;
import com.norswap.autumn.parsing.extensions.cluster.ExpressionCluster;
import com.norswap.autumn.parsing.extensions.cluster.ExpressionCluster.Group;
import com.norswap.autumn.parsing.expressions.Whitespace;
import com.norswap.autumn.parsing.extensions.cluster.Filter;
import com.norswap.autumn.parsing.extensions.cluster.WithMinPrecedence;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursive;
import com.norswap.util.JArrays;
import java.util.Arrays;

public final class ParsingExpressionFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CAPTURES

    public static Capture capture(Decoration[] decorations, ParsingExpression operand)
    {
        return new Capture(true, false, operand, decorations);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture capture(ParsingExpression operand)
    {
        return capture(new Decoration[0], operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression capture(String name, ParsingExpression operand)
    {
        return capture($(accessor(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture captureText(Decoration[] decorations, ParsingExpression operand)
    {
        return new Capture(true, true, operand, decorations);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture captureText(ParsingExpression operand)
    {
        return captureText(new Decoration[0], operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression captureText(String name, ParsingExpression operand)
    {
        return captureText($(accessor(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture set(Decoration[] decorations, ParsingExpression operand)
    {
        return new Capture(false, false, operand, decorations);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture marker(Decoration[] decorations)
    {
        return capture(decorations, new Success());
    }

    // ---------------------------------------------------------------------------------------------

    public static Decoration[] $(Decoration... decorations)
    {
        return decorations;
    }

    // ---------------------------------------------------------------------------------------------

    public static Decoration tag(String tag)
    {
        return new TagDecoration(tag);
    }

    // ---------------------------------------------------------------------------------------------

    public static Decoration accessor(String accessor)
    {
        return new AccessorDecoration(accessor);
    }

    // ---------------------------------------------------------------------------------------------

    public static Decoration group(String group)
    {
        return new GroupDecoration(group);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression tag(String name, ParsingExpression operand)
    {
        return set($(tag(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression accessor(String name, ParsingExpression operand)
    {
        return set($(accessor(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression group(String name, ParsingExpression operand)
    {
        return set($(group(name)), operand);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Any any()
    {
        return new Any();
    }

    // ---------------------------------------------------------------------------------------------

    public static CharRange charRange(char start, char end)
    {
        CharRange result = new CharRange();
        result.start = start;
        result.end = end;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static CharSet charSet(char[] chars)
    {
        CharSet result = new CharSet();
        result.chars = chars;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static CharSet charSet(String chars)
    {
        return charSet(chars.toCharArray());
    }

    // ---------------------------------------------------------------------------------------------

    public static Choice choice(ParsingExpression... operands)
    {
        Choice result = new Choice();
        result.operands = operands;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Dumb dumb(ParsingExpression operand)
    {
        Dumb result = new Dumb();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression dumb(ParsingExpression... seq)
    {
        return dumb(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static ExpressionCluster cluster(Group... groups)
    {
        ExpressionCluster result = new ExpressionCluster();

        // Sort in decreasing order of precedence.
        Arrays.sort(groups, (g1, g2) -> g2.precedence - g1.precedence);

        result.groups = groups;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprDropPrecedence(ParsingExpression operand)
    {
        return exprWithMinPrecedence(0, operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprDropPrecedence(ParsingExpression... seq)
    {
        return exprWithMinPrecedence(0, sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprWithMinPrecedence(int minPrecedence, ParsingExpression operand)
    {
        WithMinPrecedence result = new WithMinPrecedence();
        result.operand = operand;
        result.minPrecedence = minPrecedence;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprWithMinPrecedence(int minPrecedence, ParsingExpression... seq)
    {
        return exprWithMinPrecedence(minPrecedence, sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Group group(int precedence, boolean leftRecursive, boolean leftAssociative, ParsingExpression... alternates)
    {
        Group group = new Group();
        group.precedence = precedence;
        group.leftRecursive = leftRecursive;
        group.leftAssociative = leftAssociative;
        group.operands = alternates;
        return group;
    }

    // ---------------------------------------------------------------------------------------------

    public static Group group(int precedence, ParsingExpression... alternates)
    {
        return group(precedence, false, false, alternates);
    }

    // ---------------------------------------------------------------------------------------------

    public static Group groupLeftRec(int precedence, ParsingExpression... alternates)
    {
        return group(precedence, true, false, alternates);
    }

    // ---------------------------------------------------------------------------------------------

    public static Group groupLeftAssoc(int precedence, ParsingExpression... alternates)
    {
        return group(precedence, true, true, alternates);
    }

    // ---------------------------------------------------------------------------------------------

    public static Filter filter(
        ParsingExpression cluster,
        ParsingExpression[] allowed,
        ParsingExpression[] forbidden)
    {
        Filter filter = new Filter();
        filter.operand = cluster;
        filter.allowed = allowed != null ? allowed : new ParsingExpression[0];
        filter.forbidden = forbidden != null ? forbidden : new ParsingExpression[0];
        return filter;
    }

    // ---------------------------------------------------------------------------------------------

    public static Filter allow$(ParsingExpression cluster, ParsingExpression... allowed)
    {
        if (cluster instanceof Filter)
        {
            Filter f = (Filter) cluster;
            f.allowed = JArrays.concat(f.allowed, allowed);
            return f;
        }

        return filter(cluster, allowed, null);
    }

    // ---------------------------------------------------------------------------------------------

    public static Filter forbid$(ParsingExpression cluster, ParsingExpression... forbidden)
    {
        if (cluster instanceof Filter)
        {
            Filter f = (Filter) cluster;
            f.forbidden = JArrays.concat(f.forbidden, forbidden);
            return f;
        }

        return filter(cluster, null, forbidden);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Use to create the allowed and forbidden parameters to {@link #filter}.
     */
    public static ParsingExpression[] $(ParsingExpression... exprs)
    {
        return exprs;
    }

    // ---------------------------------------------------------------------------------------------

    public static Literal literal(String string)
    {
        Literal result = new Literal();
        result.string = string;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftAssociative(ParsingExpression operand)
    {
        LeftRecursive result = new LeftRecursive();
        result.operand = operand;
        result.leftAssociative = true;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftAssociative(ParsingExpression... seq)
    {
        return leftAssociative(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftRecursive(ParsingExpression operand)
    {
        LeftRecursive result = new LeftRecursive();
        result.operand = operand;
        result.name = operand.name;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftRecursive(ParsingExpression... seq)
    {
        return leftRecursive(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static LongestMatch longestMatch(ParsingExpression... operands)
    {
        LongestMatch result = new LongestMatch();
        result.operands = operands;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Lookahead lookahead(ParsingExpression operand)
    {
        Lookahead result = new Lookahead();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Lookahead lookahead(ParsingExpression... seq)
    {
        return lookahead(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Memo memo(ParsingExpression operand)
    {
        Memo result = new Memo();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression memo(ParsingExpression... seq)
    {
        return memo(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Not not(ParsingExpression operand)
    {
        Not result = new Not();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Not not(ParsingExpression... seq)
    {
        return not(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence noPrecedence(ParsingExpression operand)
    {
        Precedence result = new Precedence();
        result.precedence = Precedence.NONE;
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence noPrecedence(ParsingExpression... seq)
    {
        return noPrecedence(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static OneMore oneMore(ParsingExpression operand)
    {
        OneMore result = new OneMore();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static OneMore oneMore(ParsingExpression... seq)
    {
        return oneMore(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Optional optional(ParsingExpression operand)
    {
        Optional result = new Optional();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Optional optional(ParsingExpression... seq)
    {
        return optional(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence precedence(int precedence, ParsingExpression operand)
    {
        Precedence result = new Precedence();
        result.precedence = precedence;
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence precedence(int precedence, ParsingExpression... operands)
    {
        return precedence(precedence, sequence(operands));
    }

    // ---------------------------------------------------------------------------------------------

    public static Reference reference(String target)
    {
        Reference result = new Reference();
        result.target = target;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Sequence sequence(ParsingExpression... operands)
    {
        Sequence result = new Sequence();
        result.operands = operands;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Token token(ParsingExpression operand)
    {
        Token result = new Token();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Token token(String string)
    {
        Token result = new Token();
        result.operand = literal(string);
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Token token(ParsingExpression... seq)
    {
        return token(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Whitespace whitespace()
    {
        return new Whitespace();
    }

    // ---------------------------------------------------------------------------------------------

    public static ZeroMore zeroMore(ParsingExpression operand)
    {
        ZeroMore result = new ZeroMore();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static ZeroMore zeroMore(ParsingExpression... seq)
    {
        return zeroMore(sequence(seq));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression notCharSet(String chars)
    {
        return sequence(not(charSet(chars)), any());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression until(ParsingExpression op1, ParsingExpression op2)
    {
        return sequence(zeroMore(not(op2), op1), op2);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression aloUntil(ParsingExpression op1, ParsingExpression op2)
    {
        return sequence(oneMore(not(op2), op1), op2);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression separated(ParsingExpression op, ParsingExpression sep)
    {
        return optional(op, zeroMore(sep, op));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression aloSeparated(ParsingExpression op, ParsingExpression sep)
    {
        return sequence(op, zeroMore(sep, op));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression named$(String name, ParsingExpression pe)
    {
        pe.name = name;
        return pe;
    }

    // ---------------------------------------------------------------------------------------------

    public static Debug debug(String id)
    {
        return new Debug(id);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
