package com.norswap.autumn.test.languages.clike;

import static com.norswap.autumn.ParsingExpressionFactory.captureText;
import com.norswap.autumn.capture.ParseTree;
import com.norswap.autumn.extensions.SyntaxExtension;
import com.norswap.autumn.support.GrammarCompiler;
import com.norswap.autumn.support.MetaGrammar;

public class CLikeSyntaxExtension extends SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public CLikeSyntaxExtension(String name)
    {
        super(Type.EXPRESSION, name, MetaGrammar.parsingExpression);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object compile(GrammarCompiler compiler, ParseTree tree)
    {
        switch (name)
        {
            case "TYPEDEF":
                return new TypeDef(captureText(compiler.compilePE(tree.child())));

            case "TYPEUSE":
                return new TypeUse(captureText(compiler.compilePE(tree.child())));

            default:
                throw new Error("Unknown name: " + name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
