package com.norswap.autumn.capture;

/**
 * Sets the group the node belongs to. This means the node will end up belonging to the group
 * appearing the higher in the parse tree.
 */
public class DecorateWithGroup implements Decorate
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final String group;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DecorateWithGroup(String group)
    {
        this.group = group;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void decorate(ParseTree tree)
    {
        tree.accessor = group;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return "group(" + group + ")";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
