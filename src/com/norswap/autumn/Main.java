package com.norswap.autumn;

import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.Source;
import com.norswap.autumn.test.parsing.JavaGrammar;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        String filename = args[0];

        try
        {
            Source source = Source.fromFile(filename);
            Parser parser = new Parser(source);
            parser.parse(JavaGrammar.root);
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + filename);
        }
    }
}
