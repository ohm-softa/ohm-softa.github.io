package generics;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/**
 * PECS: producer extends, consumer super.
 * This mnemonic refers to the collection's point of view when reading from or
 * writing to it.
 */
public class PECS {
    static class A {}
    static class B extends A {}
    static class C extends B {}

    public static <T> void copy(List<? extends T> from, List<? super T> to, T o) {
        for (T i : from)
            to.add(i);
    }

    public static void main(String[] args) {
        List<A> as = Arrays.asList(new A(), new A());
        List<B> bs = Arrays.asList(new B(), new B());
        List<C> cs = Arrays.asList(new C(), new C());

        copy(bs, as, new A());
        copy(bs, as, new B());

    }
}
