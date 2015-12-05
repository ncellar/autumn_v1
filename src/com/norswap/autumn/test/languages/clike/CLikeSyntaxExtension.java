package com.norswap.autumn.test.languages.clike;

import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.extensions.SyntaxExtension;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.MetaGrammar;

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
                return new TypeDef(compiler.compilePE(tree.child()));

            case "TYPEUSE":
                return new TypeUse(compiler.compilePE(tree.child()));

            default:
                throw new Error("Unknown name: " + name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
