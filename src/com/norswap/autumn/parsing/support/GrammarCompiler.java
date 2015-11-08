package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.GrammarBuilder;
import com.norswap.autumn.parsing.tree.ParseTree;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.ExpressionCluster.Group;
import com.norswap.autumn.parsing.expressions.Filter;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.util.Array;
import com.norswap.util.Streams;

import java.util.List;
import java.util.function.Function;

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

    public static GrammarBuilder compile(ParseTree tree)
    {
        Array<ParsingExpression> exprs = new GrammarCompiler().run(tree);

        ParsingExpression whitespace = exprs.stream()
            .filter(rule -> "Spacing".equals(rule.name))
            .findFirst().orElse(Whitespace.DEFAULT());

        // TODO enable setting whitespace & root from grammar file

        return Grammar.fromRoot(exprs.get(0))
            .rules(exprs)
            .whitespace(whitespace);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     *
     * Note that the references inside these expressions are not resolved.
     */
    public Array<ParsingExpression> run(ParseTree tree)
    {
        Array<ParsingExpression> out = new Array<>(
            Streams.from(tree.group("rules"))
                .map(this::compileRule).toArray(ParsingExpression[]::new));

        out.addAll(namedClusterAlternates);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileRule(ParseTree rule)
    {
        ParsingExpression topChoice = rule.has("cluster")
            ? compileCluster(rule.value("ruleName"), rule.get("cluster"))
            : compilePE(rule.get("expr").child());

        return decorateAssignment(rule, topChoice);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression decorateAssignment(ParseTree assignment, ParsingExpression pe)
    {
        if (assignment.has("dumb"))
        {
            pe = dumb(pe);
        }

        if (assignment.has("token"))
        {
            pe = token(pe);
        }

        String ruleName = assignment.value("ruleName");
        pe = compileCapture(ruleName, pe, assignment.group("captureSuffixes"));
        return named$(ruleName, pe);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileOneOrGroup(
        Compiler itemCompiler, Grouper grouper, ParseTree tree)
    {
        if (tree.children().size() == 1)
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

    private ParsingExpression compileCluster(String clusterName, ParseTree expression)
    {
        Array<Group> groups = new Array<>(group(1, false, false));
        int precedence = 1;
        Array<ParsingExpression> alts = new Array<>();

        for (ParseTree alt: expression.group("alts"))
        {
            if (alt.hasTag("directive"))
            {
                if (!alts.isEmpty())
                {
                    groups.peek().operands = alts.toArray(ParsingExpression[]::new);
                    alts = new Array<>();
                    ++precedence;
                }
                else
                {
                    groups.pop();
                }

                switch (alt.value)
                {
                    case "@+":
                        groups.push(group(precedence, false, false));
                        break;

                    case "@+_left_recur":
                        groups.push(group(precedence, true, false));
                        break;

                    case "@+_left_assoc":
                        groups.push(group(precedence, true, true));
                        break;

                    default:
                        throw new Error("unknown directive");
                }

                continue;
            }

            // arrows

            ParsingExpression pe = compilePE(alt.get("expr").child());

            if (alt.has("ruleName"))
            {
                pe = decorateAssignment(alt, pe);
                pe = named$(clusterName + "." + pe.name, pe);

                namedClusterAlternates.push(pe);
            }

            alts.push(pe);
        }

        if (!alts.isEmpty())
        {
            groups.peek().operands = alts.toArray(ParsingExpression[]::new);
        }
        else
        {
            groups.pop();
        }

        return cluster(groups.toArray(Group[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression compileRef(ParseTree tree)
    {
        Reference ref = reference(tree.value("name"));

        List<ParseTree> allowed = tree.group("allowed");
        List<ParseTree> forbidden = tree.group("forbidden");

        if (!allowed.isEmpty() || !forbidden.isEmpty())
        {
            return filter(
                Streams.from(allowed)
                    .map(pe -> reference(ref.target + "." + pe.value))
                    .toArray(ParsingExpression[]::new),

                Streams.from(forbidden)
                    .map(pe -> reference(ref.target + "." + pe.value))
                    .toArray(ParsingExpression[]::new),

                ref
            );
        }
        else {
            return ref;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private String name(String name, ParsingExpression expr, ParseTree suffix)
    {
        if (suffix.has("name"))
        {
            return suffix.value("name");
        }

        // Else there is a dollar instead of a name.
        // Either this qualifies a rule ...

        if (name != null)
        {
            return name;
        }

        // ... or a reference (possibly wrapped in a filter).

        if (expr == null)
        {
            throw new RuntimeException(
                "Dollar ($) capture name used in conjuction with a marker.");
        }

        if (expr instanceof Filter)
        {
            expr = ((Filter) expr).operand;
        }

        if (!(expr instanceof Reference))
        {
            throw new RuntimeException(
                "Dollar ($) capture name is a suffix of something which is not an identifier");
        }

        return ((Reference)expr).target;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The name field is for rule names, if this is a capture over a rule definition.
     * <p>
     * If the child field is null, this is a marker capture.
     */
    private ParsingExpression compileCapture
        (String name, ParsingExpression child, List<ParseTree> suffixes)
    {
        boolean first = true;

        // A marker implies capture!
        ParsingExpression out = child == null
            ? capture((first = false), null)
            : child;

        int accessors = 0;

        for (ParseTree suffix: suffixes)
        {
            suffix = suffix.child();

            switch (suffix.accessor)
            {
                case "capture":
                    if (!first)
                    {
                        throw new RuntimeException("Capture suffix (:) not appearing as first suffix.");
                    }
                    out = capture(suffix.has("captureText"), out);
                    break;

                case "accessor":
                    out = accessor$(name(name, child, suffix), out);
                    ++accessors;
                    break;

                case "group":
                    out = group$(name(name, child, suffix), out);
                    ++accessors;
                    break;

                case "tag":
                    out = tag$(name(name, child, suffix), out);
                    break;

                default:
                    throw new RuntimeException("Unknown capture type: " + suffix.accessor);
            }

            first = false;
        }

        if (accessors > 1)
        {
            throw new RuntimeException("More than one accessor or group specification.");
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression[] compileChildren(ParseTree tree)
    {
        return tree.children().stream()
            .map(this::compilePE)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePE(ParseTree tree)
    {
        ParseTree child;
        ParsingExpression childPE;

        switch (tree.accessor)
        {
            case "choice":
                return choice(compileChildren(tree));

            case "sequence":
                return sequence(compileChildren(tree));

            case "and":
                return lookahead(compilePE(tree.child()));

            case "not":
                return not(compilePE(tree.child()));

            case "token":
                return token(compilePE(tree.child()));

            case "dumb":
                return dumb(compilePE(tree.child()));

            case "until":
                return until(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "aloUntil":
                return aloUntil(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "separated":
                return separated(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "aloSeparated":
                return aloSeparated(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "optional":
                return optional(compilePE(tree.child()));

            case "zeroMore":
                return zeroMore(compilePE(tree.child()));

            case "oneMore":
                return oneMore(compilePE(tree.child()));

            case "capture":
                child = tree.child(0);
                childPE = child.accessor.equals("marker") ? null : compilePE(child);
                return compileCapture(null, childPE, tree.group("captureSuffixes"));

            case "drop":
                return exprDropPrecedence(compilePE(tree.child()));

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
                throw new RuntimeException("Parsing expression with unknown name: " + tree.accessor);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
