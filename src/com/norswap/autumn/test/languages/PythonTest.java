package com.norswap.autumn.test.languages;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.extensions.bruteforcetree.BruteForceTreeExtension;
import com.norswap.autumn.parsing.extensions.tracer.TracerExtension;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.test.languages.python.PythonExtension;
import com.norswap.util.Glob;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PythonTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String grammarFile = "grammars/Python.autumn";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        Grammar grammar = Grammar.fromSource(Source.fromFile(grammarFile).columnStart(1).build())
            .withExtension(new PythonExtension())
            //.withExtension(new TracerExtension())
            //.withExtension(new BruteForceTreeExtension())
            .build();

        for (Path path: Glob.glob("**/*.py", Paths.get("../django-1.9")))
        {
            /*
            if (!path.toString().equals("..\\django-1.9\\tests\\view_tests\\tests\\py3_test_debug.py"))
                continue;
            //*/

            // These are templates to be preprocessed or python 3 syntax.
            if (path.startsWith("..\\django-1.9\\django\\conf\\app_template")
            ||  path.startsWith("..\\django-1.9\\tests\\template_tests")
            ||  path.toString().startsWith("..\\django-1.9\\tests\\view_tests\\tests\\py3"))
                continue;

            System.err.println(path);
            ParseResult result = Autumn.parseFile(grammar, path.toString());

            if (!result.matched)
            {
                System.err.println(result.error.message());

                return;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
