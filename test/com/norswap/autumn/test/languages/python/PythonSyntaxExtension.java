package com.norswap.autumn.test.languages.python;

import com.norswap.autumn.capture.ParseTree;
import com.norswap.autumn.extensions.SyntaxExtension;
import com.norswap.autumn.support.GrammarCompiler;
import static com.norswap.autumn.ParsingExpressionFactory.succeed;
import com.norswap.autumn.support.MetaGrammar;

public class PythonSyntaxExtension extends SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public PythonSyntaxExtension(String name)
    {
        super(Type.EXPRESSION, name,
            name.equals("TOKEN")
                ? MetaGrammar.parsingExpression
                : succeed(),
            false);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object compile(GrammarCompiler compiler, ParseTree tree)
    {
        return name.equals("TOKEN")
            ? new PythonToken(compiler.compilePE(tree.child()))
            : new PythonIndentToken(name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
