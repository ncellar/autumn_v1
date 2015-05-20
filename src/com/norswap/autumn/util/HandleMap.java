package com.norswap.autumn.util;

public final class HandleMap
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static int INITIAL_SIZE = 4;
    public static float LOAD_FACTOR = 0.5f;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final class Entry
    {
        int handle;
        Object value;

        Entry(int handle, Object value)
        {
            this.handle = handle;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return handle + ": " + value;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Entry[] array = new Entry[INITIAL_SIZE];
    private int load;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int getIndex(int handle)
    {
        int index = (array.length - 1) & handle;

        Entry entry = array[index];

        while (entry != null && entry.handle != handle)
        {
            if (++index == array.length)
            {
                index = 0;
            }

            entry = array[index];
        }

        return entry == null
            ? -index - 1
            : index;
    }

    // ---------------------------------------------------------------------------------------------

    public <T> T get(int handle)
    {
        int index = getIndex(handle);

        return index >= 0
            ? Caster.cast(array[index].value)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    public void set(int handle, Object value)
    {
        int index = getIndex(handle);

        if (index >= 0)
        {
            array[index].value = value;
        }
        else
        {
            if (array.length * LOAD_FACTOR < load + 1)
            {
                grow();
                index = getIndex(handle);
            }
            else
            {
                index = -index - 1;
            }

            array[index] = new Entry(handle, value);
            ++load;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void unset(int handle)
    {
        int index = getIndex(handle);

        if (index >= 0)
        {
            array[index] = null;
            --load;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private void grow()
    {
        Entry[] oldArray = array;
        this.array = new Entry[array.length * 2];

        for (Entry entry: oldArray)
        {
            if (entry != null)
            {
                array[getIndex(entry.handle)] = entry;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
