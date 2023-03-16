package fplive;




import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.function.*;

import static fplive.List.empty;
import static fplive.List.list;

public class Example {

    // 1, 2, 3 => "(1 (2 (3 nil)))"


    static <T> boolean contains(List<T> xs, T x) {
        if (xs.isEmpty()) return false;
        else if (xs.head.equals(x)) return true;
        else return contains(xs.tail, x);
    }

    static int length(List<?> xs) {
        if (xs.isEmpty()) return 0;
        else return 1 + length(xs.tail);
    }

    // take([a, b, c], 2) => [a, b]
    // => [a, take([b, c], 1)]
    // => [a, b, take([c], 0)]
    // => [a, b, nil]
    static <T> List<T> take(List<T> xs, int n) {
        if (n <= 0 || xs.isEmpty()) return empty();
        else return list(xs.head, take(xs.tail, n-1));
    }

    // drop([a, b, c], 2) => [c]
    // => drop([b, c], 1)
    // => drop([c], 0) => [c]
    static <T> List<T> drop(List<T> xs, int n) {
        if (n <= 0 || xs.isEmpty()) return xs;
        else return drop(xs.tail, n-1);
    }

    // append([a, b, c], [d, e]) => [a, b, c, d, e]
    // => [a, append([b, c], [d, e])]
    // => [a, b, append([c], [d, e])]
    // => [a, b, c, append([], [d, e])]
    // => [a, b, c, d, e]
    static <T> List<T> append(List<T> xs, List<T> ys) {
        if (xs.isEmpty()) return ys;
        else return list(xs.head, append(xs.tail, ys));
    }

    // reverse([a, b, c]) => [c, b, a]
    // => append(reverse([b, c]), a)
    // => append( append(reverse([c]), b) , a)
    // => append( append( append(reverse([]), c), b, a)
    // => append( append( append( [], c), b), a)
    // => append( append( [c], b), a)
    // => append( [c, b], a)
    // => [c, b, a]
    static <T> List<T> reverse(List<T> xs) {
        if (xs.isEmpty()) return xs;
        else return append(reverse(xs.tail), list(xs.head, empty()));
    }

    static <T extends Comparable<T>> List<T> msort(List<T> xs) {
        if (xs.isEmpty()) return xs;
        else if (xs.tail.isEmpty()) return xs;
        else {
            int n = length(xs);
            List<T> li = take(xs, n/2);
            List<T> re = drop(xs, n/2);

            return merge(msort(li), msort(re));
        }
    }

    // msort([3, 1, 2, 4])
    // => merge(msort([3, 1]), msort([2, 4]))
    // => merge(merge(msort([3]), msort([1])), merge(msort([2]), msort([4])))
    // => merge(merge([3], [1]), merge([2], [4]))
    // => merge([1, 3], [2, 4])
    // => [1, 2, 3, 4]

    static <T extends Comparable<T>> List<T> merge(List<T> xs, List<T> ys) {
        if (xs.isEmpty()) return ys;
        else if (ys.isEmpty()) return xs;
        else {
            if (xs.head.compareTo(ys.head) < 0)
                return list(xs.head, merge(xs.tail, ys));
            else
                return list(ys.head, merge(xs, ys.tail));
        }
    }

    // 1, 1, 2, 3, 5, 8, 13, 21, ...
    static int fib(int n) {
        if (n < 2) return n;
        else return fib(n-1) + fib(n-2);
    }

    // fib(1, 1, 4)
    // fib(1, 1+1, 4-1)=fib(1, 2, 3)
    // fib(2, 2+1, 3-1)=fib(2, 3, 2)
    // fib(3, 3+2, 2-1)=fib(3, 5, 1)
    // 5
    static int fib(int a, int b, int n) {
        if (n == 0) return a;
        else if (n == 1) return b;
        else return fib(b, a+b, n-1);
    }

    static void print(List<?> xs) {
        if (xs.isEmpty()) return;
        else {
            System.out.println(xs.head);
            print(xs.tail);
        }
    }

    static <T> void forEach(List<T> xs, Consumer<T> c) {
        if (xs.isEmpty()) return;
        else {
            c.accept(xs.head);
            forEach(xs.tail, c);
        }
    }

    static <T> List<T> filter(List<T> xs, Predicate<T> p) {
        if (xs.isEmpty()) return empty();
        else if (p.test(xs.head)) {
            return list(xs.head, filter(xs.tail, p));
        } else {
            return filter(xs.tail, p);
        }
    }

    static <T, R> List<R> map(List<T> xs, Function<T, R> f) {
        if (xs.isEmpty()) return List.<R>empty();
        else return list(f.apply(xs.head), map(xs.tail, f));
    }

    static String toString(List<?> xs) {
        if (xs.isEmpty()) return "nil";  // case Nil => Nil
        else return "(" + xs.head + " " + toString(xs.tail) + ")";
    }

    // sum(list(1, 2, 3))
    // > 1 + sum(2, 3)
    // > 1 + 2 + sum(3)
    // > 1 + 2 + 3 + sum()
    // > 1 + 2 + 3 + 0
    // > 6
    static int sum(List<Integer> xs) {
        if (xs.isEmpty()) return 0;
        else {
            int tail = sum(xs.tail);
            return xs.head + tail;
        }
    }



    static int sumtr(List<Integer> xs, int z) {
        if (xs.isEmpty()) return z;
        else {
            return sumtr(xs.tail, z + xs.head);
        }
    }

    // multtr([1, 2, 3], 1)
    // > multtr([2, 3], 1 * 1)
    // > multtr([3], 1 * 2)
    // > multtr([], 2 * 3)
    // > 6

    static int multtr(List<Integer> xs, int z) {
        if (xs.isEmpty()) return z;
        else {
            int tmp = z * xs.head;
            return sumtr(xs.tail, tmp);
        }
    }

    static String toString(List<?> xs, String z) {
        if (xs.isEmpty()) return z;
        else {
            String tmp = z + xs.head;
            return toString(xs.tail, tmp);
        }
    }

    static <T> T reduce(List<T> xs, T z, BinaryOperator<T> op) {
        if (xs.isEmpty()) return z;
        else {
            T tmp = op.apply(z, xs.head);
            return reduce(xs.tail, tmp, op);
        }
    }

    static BigInteger reduce(List<Integer> xs, BigInteger z) {
        if (xs.isEmpty()) return z;
        else {
            return reduce(xs.tail, z.add(BigInteger.valueOf(xs.head)));
        }
    }

    static BigInteger myadd(BigInteger bi, Integer i) {
        return bi.add(BigInteger.valueOf(i));
    }



    // foldl([1, 2, 3], 0)
    // > foldl([2, 3], 0+1)
    // > foldl([3], 0+1+2)
    // > foldl([], 0+1+2+3)
    // > 6

    static <T, R> R foldl(List<T> xs, R z, BiFunction<R, T, R> op) {
        if (xs.isEmpty()) return z;
        else return foldl(xs.tail, op.apply(z, xs.head), op);
    }

    static BigInteger sum(List<Integer> xs, BigInteger z) {
        if (xs.isEmpty()) return z;
        else {
            BigInteger lo = BigInteger.valueOf(xs.head);
            BigInteger ro = sum(xs.tail, z);
            return lo.add(ro);
        }
    }

    static <T, R> R foldr(List<T> xs, R z, BiFunction<T, R, R> op) {
        if (xs.isEmpty()) return z;
        else {
            return op.apply(xs.head, foldr(xs.tail, z, op));
        }
    }

    public static void main(String[] args) {
        java.util.List<Integer> xs = java.util.List.of(1, 2, 4, 5, 6);

        xs.stream()
            .max(Integer::compare)
            .ifPresentOrElse(System.out::println, () -> {
                System.out.println("no max!");
            });

    }
}





















