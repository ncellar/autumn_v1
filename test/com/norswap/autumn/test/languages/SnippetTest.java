package com.norswap.autumn.test.languages;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.Grammar;
import com.norswap.autumn.ParseResult;
import com.norswap.autumn.ParsingExpression;
import static com.norswap.autumn.ParsingExpressionFactory.*;
import static com.norswap.autumn.extensions.cluster.ClusterExpressionFactory.exprDropPrecedence;
import com.norswap.autumn.extensions.cluster.ClusterExtension;
import com.norswap.autumn.source.Source;
import java.io.IOException;

public final class SnippetTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String
        grammarFile = "src/com/norswap/autumn/test/languages/snippet/Grammar",
        testFile = "src/com/norswap/autumn/test/languages/snippet/Test";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        ClusterExtension cext = new ClusterExtension();
        int i = 0;
        ParsingExpression
        Identifier = token(oneMore(charRange('a', 'z'))),
        E = reference("Expression"),
        Expression = named$("Expression", cext.cluster(
            groupLeftAssoc(++i, choice(
                sequence(E, token("*"), E),
                sequence(E, token("/"), E))),
            groupLeftAssoc(++i, choice(
                sequence(E, token("+"), E),
                sequence(E, token("-"), E))),
            group(++i, choice(
                Identifier,
                token(oneMore(charRange('0', '9'))),
                sequence(token("("), exprDropPrecedence(E), token(")")))))),

        Assignment = sequence(Identifier, token("="), Expression, token(";")),
        Print = sequence(token("print"), Identifier, token(";")),
        Statement = choice(Assignment, Print),
        Root = zeroMore(Statement);

        Source source = Source.fromFile(grammarFile).columnStart(1).build();
        Grammar grammar = Grammar.fromSource(source)
            .withExtension(cext)
            //.withExtension(new TracerExtension())
            //.withExtension(new BruteForceTreeExtension())
            .build();

        ParseResult result = Autumn.parseSource(grammar, Source.fromFile(testFile).columnStart(1).build());

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
