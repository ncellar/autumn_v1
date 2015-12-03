package com.norswap.autumn.test;

import com.norswap.util.Caster;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * A space to try stuff.
 */
public final class Scratchpad
{
    private static final Unsafe unsafe;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        }
        catch (Exception e) {
            throw new Error("get unsafe: no can do");
        }
    }

    public static long addressOf(Object o) throws Exception
    {
        Object[] array = new Object[] {o};

        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize)
        {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }

        return(objectAddress);
    }

    public static void main(String[] args) throws Exception
    {
//        long intClassAddress = addressOf(Integer.class);
//        long strClassAddress = addressOf(String.class);
//        System.err.println(intClassAddress + "/" + strClassAddress);
//        // not working
//        unsafe.putAddress(intClassAddress + 32, strClassAddress);
//        String x = (String) (Object) (new Integer(666));

        Object b     = new B();
        Object c     = new C();

        // Valid for a 64 bit JVM with compressed oops
        // Might not work for different architectures
        long offset  = 8L;

        // Do not keep this around for long, it changes over time
        int klass    = unsafe.getInt(b, offset);

        System.err.println (b instanceof B);
        System.err.println (c instanceof C);

        unsafe.putInt(c, offset, klass);

        System.err.println (b instanceof B);
        // If this code gets JITed, the following line may terminate the JVM with SIGSEGV
        System.err.println (c instanceof B);

        B bc = Caster.cast(c);
        System.err.println(bc.x);
    }

    static class A {}
    static class B extends A { int x = 1; }
    static class C extends A { int x = 2; }
}