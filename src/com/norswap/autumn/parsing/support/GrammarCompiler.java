package com.norswap.autumn.parsing.support;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.expressions.ExpressionCluster.Group;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.util.Array;
import com.norswap.util.Counter;
import com.norswap.util.Streams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.util.StringEscape.unescape;

public final class GrammarCompiler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    private interface Compiler extends Function<ParseTree, ParsingExpression> {}

    @FunctionalInterface
    private interface Grouper extends Function<ParsingExpression[], ParsingExpression> {}

    private static ParsingExpressionFactory F = new ParsingExpressionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Array<ParsingExpression> namedClusterAlternates = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Grammar compile(ParseTree tree)
    {
        ParsingExpression[] exprs = new GrammarCompiler().run(tree);

        ParsingExpression whitespace = Arrays.stream(exprs)
            .filter(rule -> "Spacing".equals(rule.name()))
            .findFirst().orElse(Whitespace.DEFAULT());

        // TODO enable setting whitespace & root from grammar file

        return Autumn.grammarFromExpression(
            exprs[0], Arrays.asList(exprs), whitespace, true, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     *
     * Note that the references inside these expressions are not resolved.
     */
    public ParsingExpression[] run(ParseTree tree)
    {
        ParseTree rules = tree.group("rules");

        return Stream.concat(
                Streams.from(rules).map(this::compileRule),
                Streams.from(namedClusterAlternates))
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileRule(ParseTree rule)
    {
        String ruleName = rule.value("ruleName");

        ParsingExpression topChoice = rule.has("cluster")
            ? compileExpression(rule.get("cluster"))
            : compileParsingExpression(rule.get("expr").child());

        if (rule.has("dumb"))
        {
            topChoice = dumb(topChoice);
        }

        if (rule.has("token"))
        {
            topChoice = token(topChoice);
        }

        return named$(ruleName, topChoice);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileOneOrGroup(
        Compiler itemCompiler, Grouper grouper, ParseTree tree)
    {
        if (tree.childrenCount() == 1)
        {
            return itemCompiler.apply(tree.child());
        }
        else
        {
            return grouper.apply(Streams.from(tree)
                .map(itemCompiler)
                .toArray(ParsingExpression[]::new));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileExpression(ParseTree expression)
    {
        final int UNSET = -1;

        Counter currentPrecedence = new Counter(0);
        Array<ParsingExpression> namedAlternates = new Array<>();
        Array<Group> groups = new Array<>();
        Array<Array<ParsingExpression>> alts = new Array<>();

        for (ParseTree alt: expression.group("alts"))
        {
            ParsingExpression pe = compileParsingExpression(alt.get("expr").child());

            int precedence = UNSET;
            int psets = 0;
            boolean leftRecursive = false;
            boolean leftAssociative = false;

            for (ParseTree annotation : alt.group("annotations"))
            {
                annotation = annotation.child();

                switch (annotation.name)
                {
                    case "precedence":
                        precedence = Integer.parseInt(annotation.value);
                        ++psets;
                        break;

                    case "increment":
                        precedence = currentPrecedence.i + 1;
                        ++psets;
                        break;

                    case "same":
                        precedence = currentPrecedence.i;
                        ++psets;
                        break;

                    case "left_assoc":
                        leftRecursive = true;
                        leftAssociative = true;
                        break;

                    case "left_recur":
                        leftRecursive = true;
                        break;

                    case "name":
                        pe.setName(annotation.value);
                        namedAlternates.push(pe);
                        break;
                }
            }

            if (psets > 1)
            {
                throw new RuntimeException(
                    "Expression specifies precedence more than once.");
            }

            if (precedence == 0)
            {
                throw new RuntimeException(
                    "Precedence can't be 0. Don't use @0; or use @= in first position.");
            }

            if (precedence == UNSET)
            {
                throw new RuntimeException(
                    "Expression alternate does not specify precedence.");
            }

            if (precedence < currentPrecedence.i)
            {
                throw new RuntimeException(
                    "Alternates must be grouped by precedence in expression cluster.");
            }
            else if (precedence == currentPrecedence.i)
            {
                if (leftRecursive)
                {
                    throw new RuntimeException(
                        "Can't specify left-recursion or left-associativity on non-first "
                            + "alternate of a precedence group in an expression cluster.");
                }

                alts.get(precedence).push(pe);
            }
            else
            {
                groups.put(precedence, group(precedence, leftRecursive, leftAssociative, (Group[]) null));
                alts.put(precedence, new Array<>(pe));
                ++currentPrecedence.i;
            }
        }

        namedAlternates.forEach(namedClusterAlternates::push);

        // Build the groups

        Array<Group> groupsArray = new Array<>();

        for (int i = 0; i < groups.size(); ++i)
        {
            Group group = groups.get(i);

            if (group == null) {
                continue;
            }

            group.operands = alts.get(i).toArray(ParsingExpression[]::new);
            groupsArray.push(group);
        }

        return cluster(groupsArray.toArray(Group[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression compileRef(ParseTree tree)
    {
        Reference ref = reference(tree.value("name"));

        ParseTree allowed = tree.getOrNull("allowed");
        ParseTree forbidden = tree.getOrNull("forbidden");

        if (allowed != null || forbidden != null)
        {
            return filter(
                allowed == null
                    ? new ParsingExpression[0]
                    : Streams.from(allowed)
                    .map(pe -> reference(pe.value))
                    .toArray(ParsingExpression[]::new),

                forbidden == null
                    ? new ParsingExpression[0]
                    : Streams.from(forbidden)
                    .map(pe -> reference(pe.value))
                    .toArray(ParsingExpression[]::new),

                ref
            );
        }
        else {
            return ref;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression[] compileChildren(ParseTree tree)
    {
        return tree.children.stream()
            .map(this::compileParsingExpression)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileParsingExpression(ParseTree tree)
    {
        switch (tree.name)
        {
            case "choice":
                return choice(compileChildren(tree));

            case "sequence":
                return sequence(compileChildren(tree));

            case "and":
                return lookahead(compileParsingExpression(tree.child()));

            case "not":
                return not(compileParsingExpression(tree.child()));

            case "until":
                return until(
                    compileParsingExpression(tree.child(0)),
                    compileParsingExpression(tree.child(1)));

            case "aloUntil":
                return aloUntil(
                    compileParsingExpression(tree.child(0)),
                    compileParsingExpression(tree.child(1)));

            case "optional":
                return optional(compileParsingExpression(tree.child()));

            case "zeroMore":
                return zeroMore(compileParsingExpression(tree.child()));

            case "oneMore":
                return oneMore(compileParsingExpression(tree.child()));

            case "drop":
                return exprDropPrecedence(compileParsingExpression(tree.child()));

            case "ref":
                return compileRef(tree);

            case "any":
                return any();

            case "charRange":
                return charRange(
                    unescape(tree.value("first")).charAt(0),
                    unescape(tree.value("last")).charAt(0));

            case "charSet":
                return charSet(unescape(tree.value("charSet")));

            case "notCharSet":
                return notCharSet(unescape(tree.value("notCharSet")));

            case "stringLit":
                return literal(unescape(tree.value("literal")));

            default:
                throw new RuntimeException("Parsing expression with unknown name: " + tree.name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
