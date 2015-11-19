package com.norswap.autumn.test;

import com.norswap.util.Pair;

import java.util.function.Function;

/**
 * A space to try stuff.
 */
public final class Scratchpad
{
    public static void main(String[] args)
    {

    }

    /*

    newtype Parser r = Parser (String -> (r, String))

    parse :: Parser r -> String -> (r, String)
    parse (Parser f) = f

    bind :: Parser r1 -> (r1 -> Parser r2) -> Parser r2
    bind p f = f (parse p)
     */

    @FunctionalInterface
    interface Parser<R> extends Function<String, Pair<R, String>>
    {
        default <R2> Parser<R2> bind(Function<R, Parser<R2>> f)
        {
            return input -> {
                Pair<R, String> result = this.apply(input);
                return f.apply(result.a).apply(result.b);
            };
        }
    }
}