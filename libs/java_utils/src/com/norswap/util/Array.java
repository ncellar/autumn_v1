package com.norswap.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An enriched version of {@link java.util.ArrayList}.
 * <p>
 * TODO
 * <ul>
 *     <li>Implement Deque</li>
 *     <li>Read only subset</li>
 * </ul>
 */
public final class Array<T> implements List<T>, RandomAccess, Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Default array capacity. Users can change this property (not thread-safe!) although it is not
     * recommended. Must be > 0.
     */
    public static int DEFAULT_CAPACITY = 4;

    /**
     * The growth factor for arrays. Users can change this property (not thread-safe!) although it
     * is not recommended. Must be > 1.
     */
    public static double GROWTH_FACTOR = 2.0f;

    /**
     * The unique instance returned by {@link #empty}. Immutable.
     */
    private static final Array<Object> EMPTY = new Array<>(0);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Object[] array;
    private int next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new array using the given array as backing store. The first {@code size} elements
     * of the array are considered to be part of the array.
     */
    public Array(T[] array, int size)
    {
        assert size <= array.length;

        this.array = array;
        this.next = size;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array containing the given items.
     */
    @SafeVarargs
    public Array(T... items)
    {
        this.array = items;
        this.next = items.length;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array with the given capacity.
     */
    public Array(int capacity)
    {
        array = new Object[capacity];
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new array with the default capacity.
     */
    public Array()
    {
        assert DEFAULT_CAPACITY > 0;

        array = new Object[DEFAULT_CAPACITY];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FACTORY METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an immutable empty array. The same instance is used each time and dynamically
     * cast to the required type.
     */
    @SuppressWarnings("unchecked")
    public static <T> Array<T> empty()
    {
        return (Array<T>) EMPTY;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array containing a single item. Useful because passing a single array argument to
     * {@link #Array(Object[])} is ambiguous.
     */
    @SuppressWarnings("unchecked")
    public static <T> Array<T> fromItem(T item)
    {
        return new Array<>((T) item);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array containing the items contained withing the array. Useful because passing a
     * single array argument to {@link #Array(Object[])} is ambiguous.
     */
    @SuppressWarnings("unchecked")
    public static <T> Array<T> fromArray(T[] array)
    {
        return new Array<>((T[]) array);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Equivalent to {@link #Array(Object[])}, but allows using an instance of {@code Object[]}
     * instead of {@code T[]} as backing store.
     */
    public static <T> Array<T> fromUnsafe(Object[] array)
    {
        return fromUnsafe(array, array.length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Equivalent to {@link #Array(Object[], int)}, but allows using an instance of {@code Object[]}
     * instead of {@code T[]} as backing store.
     */
    public static <T> Array<T> fromUnsafe(Object[] array, int size)
    {
        assert size <= array.length;

        Array<T> out = new Array<>();
        out.array = array;
        out.next = size;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array of size n where each element is initialized to the given item.
     */
    public static <T> Array<T> fromItem(int n, T item)
    {
        Array<T> out = new Array<>(n);
        out.ensureSize(n);
        out.fill(item);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates an array of size n where each element is initialized to null.
     */
    public static <T> Array<T> ofSize(int n)
    {
        Array<T> out = new Array<>(n);
        out.ensureSize(n);
        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // STATIC METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an array containing a copy if the first n items of the given java array.
     */
    public static <T> Array<T> copyOf(T[] array, int n)
    {
        assert n <= array.length;

        Array<T> out = Array.ofSize(n);
        out.copy(array, 0, 0, n);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns an array containing a copy fo the [from, to[ range of the given java array.
     */
    public static <T> Array<T> copyOf(T[] array, int from, int to)
    {
        Array<T> out = Array.ofSize(to - from);
        out.copy(array, from, to, to - from);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return an array which is a concatenation of all the given arrays.
     */
    @SafeVarargs
    public static <T> Array<T> concat(Array<? extends T> ...arrays)
    {
        Array<T> out = new Array<>();

        for (Array<? extends T> array: arrays)
        {
            out.addAll(array);
        }

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COPY METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a copy of the [from, to[ range of this array.
     */
    public Array<T> copyOfRange(int from, int to)
    {
        assert to >= from;

        Array<T> out = Array.ofSize(to - from);
        out.copy(this, from, 0, to - from);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a copy of the [0, n[ range of this array.
     */
    public Array<T> copyOfPrefix(int n)
    {
        assert n >= 0 && n <= next;

        return copyOfRange(0, n);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a copy of the [from, size[ range of this array.
     */
    public Array<T> copyFrom(int from)
    {
        assert from >= 0 && from <= next;

        return copyOfRange(from, next);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a copy of the [size - n, size[ range of this array.
     */
    public Array<T> copyOfSuffix(int n)
    {
        assert n >= 0 && n <= next;

        return copyOfRange(next - n, next);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of {@code src} starting at position {@code srcPos} to the
     * position {@code dstPos} of this array. The size of this array should be {@code >= dstPos +
     * length}. Existing elements will be overwritten.
     */
    public void copy(T[] src, int srcPos, int dstPos, int length)
    {
        assert src.length >= srcPos + length;
        assert next >= dstPos + length;

        System.arraycopy(src, srcPos, array, dstPos, length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of {@code src} starting at position {@code srcPos} to the
     * position {@code dstPos} of this array. The size of this array should be {@code >= dstPos +
     * length}. Existing elements will be overwritten.
     */
    public void copy(Array<? extends T> src, int srcPos, int dstPos, int length)
    {
        assert src.next >= srcPos + length;
        assert next >= dstPos + length;

        System.arraycopy(src.array, srcPos, array, dstPos, length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of this starting at position {@code srcPos} to the position
     * {@code dstPos} of {@code dst}.
     */
    public void copyTo(T[] dst, int srcPos, int dstPos, int length)
    {
        assert dst.length >= dstPos + length;
        assert next >= srcPos + length;

        System.arraycopy(array, srcPos, dst, dstPos, length);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Copy {@code length} elements of this starting at position {@code srcPos} to the position
     * {@code dstPos} of {@code dst}. The size of {@code dst} should be {@code >= dstPos +
     * length}. Existing elements will be overwritten.
     */
    public void copyTo(Array<? super T> dst, int srcPos, int dstPos, int length)
    {
        dst.copy(this, srcPos, dstPos, length);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void fill(T value)
    {
        Arrays.fill(array, 0, next, value);
    }

    // ---------------------------------------------------------------------------------------------

    public void fill(T value, int from, int to)
    {
        assert next <= to;

        Arrays.fill(array, from, to, value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isEmpty()
    {
        return next == 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int size()
    {
        return next;
    }

    // ---------------------------------------------------------------------------------------------

    public int capacity()
    {
        return array.length;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // INSERTIONS

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean add(T t)
    {
        if (next == array.length)
        {
            ensureCapacity(array.length + 1);
        }

        array[next++] = t;

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void add(int index, T element)
    {
        if (next == array.length)
        {
            ensureCapacity(array.length + 1);
        }

        System.arraycopy(array, index, array, index + 1, next - index - 1);
        array[index] = element;
        next++;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public T get(int index)
    {
        return Caster.cast(array[index]);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public T set(int index, T t)
    {
        @SuppressWarnings("unchecked")
        T element = (T) array[index];
        array[index] = t;
        return element;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * If {@code collection} is null, it will be treated as empty.
     */
    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        int size = next;

        if (collection != null)
        {
            collection.forEach(this::add);
        }

        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean addAll(Array<? extends T> array)
    {
        int dstPos = next;
        int size = array == null ? 0 : array.size();

        if (size == 0)
        {
            return false;
        }

        ensureSize(next + size);
        copy(array, 0, dstPos, size);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * If {@code collection} is null, it will be treated as empty.
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        if (c == null || c.isEmpty())
        {
            return false;
        }

        int csize = c.size();

        if (array.length < next + csize)
        {
            ensureCapacity(next + csize);
        }

        System.arraycopy(array, index, array, index + csize, next - index - 1);

        int i = index;
        for (T t: c)
        {
            array[i++] = t;
        }

        return csize != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // NEW OPERATIONS

    // ---------------------------------------------------------------------------------------------

    public void ensureSize(int size)
    {
        if (array.length < size) {
            ensureCapacity(size);
        }

        next = size;
    }

    // ---------------------------------------------------------------------------------------------

    public void ensureCapacity(int capacity)
    {
        int size = array.length;

        if (size < capacity)
        {
            while (size < capacity)
            {
                size = (int) (size * GROWTH_FACTOR);
            }

            array = Arrays.copyOf(array, size);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        for (int i = size; i < next; ++i)
        {
            array[i] = null;
        }

        if (size < next)
        {
            next = size;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void push(T t)
    {
        add(t);
    }

    // ---------------------------------------------------------------------------------------------

    public T pop()
    {
        return Caster.cast(array[--next]);
    }

    // ---------------------------------------------------------------------------------------------

    public T popOrNull()
    {
        return next == 0 ? null : pop();
    }

    // ---------------------------------------------------------------------------------------------

    public T popOr(T t)
    {
        return next == 0 ? t : pop();
    }

    // ---------------------------------------------------------------------------------------------

    public T peek()
    {
        return Caster.cast(array[next - 1]);
    }

    // ---------------------------------------------------------------------------------------------

    public T peekOrNull()
    {
        return next == 0 ? null : peek();
    }

    // ---------------------------------------------------------------------------------------------

    public T peekOr(T t)
    {
        return next == 0 ? t : peek();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // REMOVALS

    // ---------------------------------------------------------------------------------------------

    @Override
    public T remove(int index)
    {
        @SuppressWarnings("unchecked")
        T item = (T) array[index];
        System.arraycopy(array, index + 1, array, index, next - index - 1);
        --next;
        return item;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean remove(Object t)
    {
        for (int i = 0; i < next; ++i)
        {
            if (array[i].equals(t))
            {
                remove(i);
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean removeFromEnd(T t)
    {
        for (int i = next - 1; i >= 0; --i)
        {
            if (array[i].equals(t))
            {
                remove(i);
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean removeAll(Collection<?> c)
    {
        int size = next;
        c.forEach(this::remove);
        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean retainAll(Collection<?> c)
    {
        int size = next;

        for (int i = 0; i < next; ++i)
        {
            if (!c.contains(array[i]))
            {
                remove(i--);
            }
        }

        return size != next;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void clear()
    {
        truncate(0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MISC

    // ---------------------------------------------------------------------------------------------

    @Override
    public int indexOf(Object t)
    {
        for (int i = 0; i < next; ++i)
        {
            if (t == null ? array[i] == null : t.equals(array[i]))
            {
                return i;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int lastIndexOf(Object t)
    {
        for (int i = next - 1; i >= 0; --i)
        {
            if (t == null ? array[i] == null : t.equals(array[i]))
            {
                return i;
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean contains(Object t)
    {
        return indexOf(Caster.cast(t)) >= 0;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object o: c)
        {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Array<T> subList(int from, int to)
    {
        throw new UnsupportedOperationException("Array doesn't support sublist for now");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ITERATORS

    // ---------------------------------------------------------------------------------------------

    private class ArrayIterator implements ListIterator<T>
    {
        int index;
        int direction;

        @Override
        public boolean hasNext()
        {
            return index < next;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next()
        {
            direction = 1;
            return (T) array[index++];
        }

        @Override
        public boolean hasPrevious()
        {
            return index > 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T previous()
        {
            direction = -1;
            return (T) array[--index];
        }

        @Override
        public int nextIndex()
        {
            return index;
        }

        @Override
        public int previousIndex()
        {
            return index - 1;
        }

        @Override
        public void remove()
        {
            if (direction > 0)
            {
                Array.this.remove(index - 1);
                --index;
            }
            else if (direction < 0)
            {
                Array.this.remove(index);
            }
            else {
                throw new IllegalStateException(
                    "Calling remove twice, or after add, or before calling next.");
            }

            direction = 0;
        }

        @Override
        public void set(T t)
        {
            if (direction > 0)
            {
                array[index - 1] = t;
            }
            else if (direction < 0)
            {
                array[index] = t;
            }
            else {
                throw new IllegalStateException(
                    "Calling set after remove or add, or before calling next.");
            }
        }

        @Override
        public void add(T t)
        {
            Array.this.add(index, t);
            ++index;
            direction = 0;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private final class ReverseArrayIterator extends ArrayIterator
    {
        {
            index = next - 1;
        }

        @Override
        public boolean hasNext()
        {
            return super.hasPrevious();
        }

        @Override
        public T next()
        {
            return super.previous();
        }

        @Override
        public boolean hasPrevious()
        {
            return super.hasNext();
        }

        @Override
        public T previous()
        {
            return super.next();
        }

        @Override
        public int nextIndex()
        {
            return super.previousIndex();
        }

        @Override
        public int previousIndex()
        {
            return super.nextIndex();
        }

        @Override
        public void add(T t)
        {
            Array.this.add(index, t);
            // ++index; // remove this from superclass to satisfy method contract
            direction = 0;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Iterator<T> iterator()
    {
        return new ArrayIterator();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ListIterator<T> listIterator()
    {
        return new ArrayIterator();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ListIterator<T> listIterator(int index)
    {
        ArrayIterator out = new ArrayIterator();
        out.index = index;
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public Iterable<T> reverseIterable()
    {
        return this::reverseIterator;
    }

    // ---------------------------------------------------------------------------------------------

    public ListIterator<T> reverseIterator()
    {
        return new ReverseArrayIterator();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MAP METHODS

    // TODO offer a map view on this collection

    public boolean containsValue(Object value)
    {
        return contains(value);
    }

    public boolean containsKey(Object key)
    {
        return (int) key < next;
    }

    public T get(Object key)
    {
        return get((int) key);
    }

    public T put(Integer key, T value)
    {
        if (next <= key)
        {
            ensureSize(key + 1);
        }

        return set(key, value);
    }

    public Collection<T> values()
    {
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CONVERSIONS TO JAVA ARRAYS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a Java array containing the same elements as this array. The returned array
     * will be produced by the given supplier (in general something like {@code Object[]::new}).
     */
    public T[] toArray(Function<Integer, T[]> supplier)
    {
        T[] out = supplier.apply(next);
        System.arraycopy(array, 0, out, 0, next);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object[] toArray()
    {
        Object[] out = new Object[next];
        System.arraycopy(array, 0, out, 0, next);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <U> U[] toArray(U[] out)
    {
        if (out.length < next)
        {
            out = JArrays.newInstance(out, next);
        }

        System.arraycopy(array, 0, out, 0, next);

        if (out.length != next)
        {
            out[next] = null;
        }

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OBJECT OVERRIDES
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        // part auto-generated, part lifted from Arrays.equals

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Array<?> array1 = (Array<?>) o;

        if (next != array1.next)
            return false;

        for (int i = 0; i < next; ++i)
        {
            Object o1 = array[i];
            Object o2 = array1.array[i];

            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        // lifted from Arrays.hashCode

        int result = 1;

        for (T t: this)
        {
            result = 31 * result + (t == null ? 0 : t.hashCode());
        }

        return result;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("[");

        for (int i = 0; i < next; ++i)
        {
            b.append(array[i]);
            b.append(", ");
        }

        if (next > 0)
        {
            b.setLength(b.length() - 2);
        }

        b.append("]");
        return b.toString();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings({"unchecked", "CloneDoesntCallSuperClone"})
    public Array<T> clone()
    {
        return new Array(array.clone(), next);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MAP VIEW
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A map view of the array. The map does not support null values: null values are used to
     * indicate the absence of values for indices in the [0, size[ range.
     */
    private class MapView implements Map<Integer, T>
    {
        ////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public int size()
        {
            // TODO incorrect
            return next;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean isEmpty()
        {
            // TODO incorrect
            return next == 0;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean containsKey(Object key)
        {
            int i = (int) key;
            return 0 <= i && i < next && array[i] != null;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean containsValue(Object value)
        {
            return Array.this.contains(value);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public T get(Object key)
        {
            // TODO
            return Array.this.get(key);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public T put(Integer key, T value)
        {
            // TODO
            return Array.this.put(key, value);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public T remove(Object key)
        {
            return Array.this.set((int) key, null);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public void putAll(Map<? extends Integer, ? extends T> m)
        {
            m.forEach(this::put);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public void clear()
        {
            Array.this.truncate(0);
        }

        // -----------------------------------------------------------------------------------------

        /**
         * @inheritDoc
         * <p>
         * MapView precisions: removal will leave null elements in the place of removed elements.
         */
        @Override
        public Set<Integer> keySet()
        {
            return new KeySetView();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public Collection<T> values()
        {
            return Array.this;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public Set<Entry<Integer, T>> entrySet()
        {
            return new EntrySetView();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public int hashCode()
        {
            // TODO
            return super.hashCode();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean equals(Object obj)
        {
            // TODO
            return super.equals(obj);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // KEY SET VIEW
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class KeySetView implements Set<Integer>
    {
        @Override
        public int size()
        {
            // TODO incorrect
            return next;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean isEmpty()
        {
            // TODO incorrect
            return next == 0;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean contains(Object o)
        {
            int i = (int) o;
            return 0 <= i && i < next && array[i] != null;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public Iterator<Integer> iterator()
        {
            // TODO
            return null;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public Object[] toArray()
        {
            return IntStream.range(0, next).filter(i -> array[i] != null).boxed().toArray();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public <T> T[] toArray(T[] a)
        {
            return IntStream.range(0, next).filter(i -> array[i] != null).boxed()
                .toArray(size -> JArrays.newInstance(a, size));
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean add(Integer integer)
        {
            throw new UnsupportedOperationException();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean remove(Object o)
        {
            boolean out = contains(o);

            if (out)
            {
                int i = (int) o;
                array[i] = null;
            }

            return out;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean containsAll(Collection<?> c)
        {
            for (Object o: c)
            {
                if (!contains(o))
                {
                    return false;
                }
            }

            return true;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean addAll(Collection<? extends Integer> c)
        {
            throw new UnsupportedOperationException();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean retainAll(Collection<?> c)
        {
            boolean modified = false;

            for (int i = 0; i < next; ++i)
            {
                if (!c.contains(i))
                {
                    modified = true;
                    array[i] = null;
                }
            }

            return modified;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean removeAll(Collection<?> c)
        {
            boolean modified = false;

            for (Object o: c)
            {
                if (contains(o))
                {
                    modified = true;
                    int i = (int) o;
                    array[i] = null;
                }
            }

            return modified;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public void clear()
        {
            Array.this.truncate(0);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public int hashCode()
        {
            // TODO
            return super.hashCode();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean equals(Object obj)
        {
            // TODO
            return super.equals(obj);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ENTRY SET VIEW
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class EntrySetView implements Set<Map.Entry<Integer, T>>
    {
        @Override
        public int size()
        {
            // TODO incorrect
            return next;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean isEmpty()
        {
            // TODO incorrect
            return next != 0;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean contains(Object o)
        {
            Map.Entry<Integer, T> entry = (Map.Entry<Integer, T>) o;
            int i = entry.getKey();
            return i >= 0 && i < next && array[i].equals(entry.getValue());
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public Iterator<Map.Entry<Integer, T>> iterator()
        {
            // TODO
            return null;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public Object[] toArray()
        {
            // TODO
            return new Object[0];
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public <T> T[] toArray(T[] a)
        {
            // TODO
            return null;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean add(Map.Entry<Integer, T> entry)
        {
            throw new UnsupportedOperationException();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean remove(Object o)
        {
            // TODO
            return false;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean containsAll(Collection<?> c)
        {
            // TODO
            return false;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean addAll(Collection<? extends Map.Entry<Integer, T>> c)
        {
            throw new UnsupportedOperationException();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean retainAll(Collection<?> c)
        {
            // TODO
            return false;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean removeAll(Collection<?> c)
        {
            // TODO
            return false;
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public void clear()
        {
            Array.this.truncate(0);
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public int hashCode()
        {
            // TODO
            return super.hashCode();
        }

        // -----------------------------------------------------------------------------------------

        @Override
        public boolean equals(Object obj)
        {
            // TODO
            return super.equals(obj);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
