package generics;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Example2 {
    static void print1(Collection c) {
        for (Object o : c)
            System.out.println(o);
    }

    static void print2(Collection<Object> c) {
        for (Object o : c)
            System.out.println(o);
    }

    static void print3(Collection<?> c) {
        for (Object o : c)
            System.out.println(o);
    }

    static <T> void print4(Collection<T> c) {
        for (T o : c)
            System.out.println(o);
    }

    static <T> void addOne(Collection<T> c, T v) {
        c.add(v);
    }

    static <T> void copy(Collection<? extends T> from, Collection<? super T> to) {
        for (T i : from)
            to.add(i);
    }

    public static void main(String[] args) {
        Collection<Number> c1 = new LinkedList<>();

        print1(c1);
        // print2(c1);  Collection<Number> is not compatible with Collection<Object>
        print3(c1);
        print4(c1);

        Collection<Object> c0 = new LinkedList<>();
        Collection<Integer> c2 = new LinkedList<>();

        copy(c2, c0);
    }
}
