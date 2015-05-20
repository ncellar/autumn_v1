package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.util.Array;

public final class MouseToJava
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    StringBuilder b = new StringBuilder();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void emitGrammar(ParseTree grammar)
    {
        emitRules(grammar.get("rules"));
    }

    public void emitRules(ParseTree rules)
    {
        for (ParseTree rule: rules.children)
        {
            String ruleName = rule.value("ruleName");
            b.append("ParsingExpression ");
            b.append(ruleName);
            b.append(" = named$(\"");
            b.append(ruleName);
            b.append("\", ");
            emitTopChoice(rule.get("alts").children);
            b.append(");\n\n");
        }
    }

    public void emitTopChoice(Array<ParseTree> alts)
    {
        if (alts.size() == 1)
        {
            emitSequence(alts.get(0).get("sequence"));
            return;
        }

        b.append("choice(");

        for (ParseTree alt: alts)
        {
            emitSequence(alt.get("sequence"));
            b.append(", ");
        }

        b.setLength(b.length() - 2);
        b.append(")");
    }

    public void emitChoice(ParseTree choice)
    {
        Array<ParseTree> alts = choice.children;

        if (alts.size() == 1)
        {
            emitSequence(alts.get(0));
            return;
        }

        b.append("choice(");

        for (ParseTree alt: alts)
        {
            emitSequence(alt);
            b.append(", ");
        }

        b.setLength(b.length() - 2);
        b.append(")");
    }

    public void emitSequence(ParseTree sequence)
    {
        Array<ParseTree> items = sequence.children;

        if (items.size() == 1)
        {
            emitPrefixed(items.get(0));
            return;
        }

        b.append("sequence(");

        for (ParseTree item: items)
        {
            emitPrefixed(item);
            b.append(", ");
        }

        b.setLength(b.length() - 2);
        b.append(")");
    }

    public void emitPrefixed(ParseTree prefixed)
    {
        if ("and".equals(prefixed.name))
        {
            b.append("lookahead(");
            emitSuffixed(prefixed.children.get(0));
            b.append(")");
        }
        else if ("not".equals(prefixed.name))
        {
            b.append("not(");
            emitSuffixed(prefixed.children.get(0));
            b.append(")");
        }
        else
        {
            emitSuffixed(prefixed);
        }
    }

    public void emitSuffixed(ParseTree suffixed)
    {
        if ("until".equals(suffixed.name))
        {
            b.append("until(");
            emitPrimary(suffixed.children.get(0));
            b.append(", ");
            emitPrimary(suffixed.children.get(1));
            b.append(")");
        }
        else if ("aloUntil".equals(suffixed.name))
        {
            b.append("aloUntil(");
            emitPrimary(suffixed.children.get(0));
            b.append(", ");
            emitPrimary(suffixed.children.get(1));
            b.append(")");
        }
        else if ("optional".equals(suffixed.name))
        {
            b.append("optional(");
            emitPrimary(suffixed.children.get(0));
            b.append(")");
        }
        else if ("zeroMore".equals(suffixed.name))
        {
            b.append("zeroMore(");
            emitPrimary(suffixed.children.get(0));
            b.append(")");
        }
        else if ("oneMore".equals(suffixed.name))
        {
            b.append("oneMore(");
            emitPrimary(suffixed.children.get(0));
            b.append(")");
        }
        else
        {
            emitPrimary(suffixed);
        }
    }

    public void emitPrimary(ParseTree primary)
    {
        if ("choice".equals(primary.name))
        {
            emitChoice(primary);
        }
        else if ("ref".equals(primary.name))
        {
            b.append("reference(\"");
            b.append(primary.value);
            b.append("\")");
        }
        else if ("any".equals(primary.name))
        {
            b.append("any()");
        }
        else if ("charRange".equals(primary.name))
        {
            b.append("charRange('");
            b.append(primary.value("first"));
            b.append("', '");
            b.append(primary.value("last"));
            b.append("')");
        }
        else if ("charSet".equals(primary.name))
        {
            b.append("charSet(\"");
            b.append(primary.value("charSet"));
            b.append("\")");
        }
        else if ("notCharSet".equals(primary.name))
        {
            b.append("notCharSet(\"");
            b.append(primary.value("notCharSet"));
            b.append("\")");
        }
        else if ("stringLit".equals(primary.name))
        {
            b.append("literal(\"");
            b.append(primary.value("literal"));
            b.append("\")");
        }
        else
        {
            b.append("FUCK");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
