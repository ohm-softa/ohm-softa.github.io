package fp;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static fp.List.empty;
import static fp.List.list;

public class App1 {
	// intro: List.toString()

	// basic recursions
	static <T> boolean contains(List<T> xs, T obj) {
		if (xs.isEmpty()) return false;
		else if (xs.head.equals(obj)) return true;
		else return contains(xs.tail, obj);
	}

	static <T> int length(List<T> xs) {
		if (xs.isEmpty()) return 0;
		else return 1 + length(xs.tail);
	}

	// recursion with list generation
	static <T> List<T> take(List<T> xs, int n) {
		if (n <= 0 || xs.isEmpty()) return empty();
		else return list(xs.head, take(xs.tail, n-1));
	}

	static <T> List<T> drop(List<T> xs, int n) {
		if (n <= 0 || xs.isEmpty()) return xs;
		else return drop(xs.tail, n-1);
	}

	static <T> List<T> append(List<T> xs, List<T> y) {
		if (xs.isEmpty()) return y;
		else return list(xs.head, append(xs.tail, y));
	}

	static <T> List<T> reverse(List<T> xs) {
		if (xs.isEmpty()) return xs;
		else return append(reverse(xs.tail), list(xs.head, empty()));
	}

	// advanced recursion: insertion sort
	static <T extends Comparable<T>> List<T> isort(List<T> xs) {
		if (xs.isEmpty()) return xs;
		else return insert(xs.head, isort(xs.tail));
	}

	private static <T extends Comparable<T>> List<T> insert(T x, List<T> xs) {
		if (xs.isEmpty()) return list(x, empty());
		else {
			if (x.compareTo(xs.head) < 0) return list(x, xs);
			else return list(xs.head, insert(x, xs.tail));
		}
	}

	// advanced recursion: merge sort
	static <T extends Comparable<T>> List<T> msort(List<T> xs) {
		if (xs.isEmpty()) return xs;
		else if (xs.tail.isEmpty()) return xs;
		else {
			int n = length(xs);
			List<T> a = take(xs, n/2);
			List<T> b = drop(xs, n/2);

			return merge(msort(a), msort(b));
		}
	}

	private static <T extends Comparable<T>> List<T> merge(List<T> xs, List<T> ys) {
		if (xs.isEmpty()) return ys;
		else if (ys.isEmpty()) return xs;
		else {
			if (xs.head.compareTo(ys.head) < 0)
				return list(xs.head, merge(xs.tail, ys));
			else
				return list(ys.head, merge(xs, ys.tail));
		}
	}

	// detour: tail recursion
	static long fib1(int i) {
		if (i < 2) return i;
		else return fib1(i-1) + fib1(i-2);
	}

	static long fib2(long a, long b, int i) {
		if (i == 0) return a;
		else if (i == 1) return b;
		else return fib2(b, a+b, i-1);  // tail recursive!
	}


	// functions as first-class citizens
	static <A> void forEach(List<A> xs, Consumer<A> c) {
		if (xs.isEmpty()) return;
		else {
			c.accept(xs.head);
			forEach(xs.tail, c);
		}
	}

	static <A> List<A> filter(List<A> xs, Predicate<A> p) {
		if (xs.isEmpty()) return xs;
		else if (p.test(xs.head)) return list(xs.head, filter(xs.tail, p));
		else return filter(xs.tail, p);
	}

	static <A, B> List<B> map(List<A> xs, Function<A, B> f) {
		if (xs.isEmpty()) return empty();
		else return list(f.apply(xs.head), map(xs.tail, f));
	}

	public static void main(String[] args) {
		List<Integer> xs = list(7, 3, 1, 3);

		System.out.println(xs);

		System.out.println("contains 1: " + contains(xs, 1));  // Stream.filter().any()
		System.out.println("length: " + length(xs));

		System.out.println("take 3: " + take(xs, 3));  // Stream.limit
		System.out.println("drop 3: " + drop(xs, 3));  // Stream.skip
		System.out.println("append: " + append(xs, list(0)));
		System.out.println("reverse: " + reverse(xs));

		System.out.println("isort: " + isort(xs));
		System.out.println("msort: " + msort(xs));


		forEach(xs, i -> System.out.println(i));
		forEach(xs, System.out::println);

		System.out.println("lt 5: " + filter(xs, x -> x < 5));  // Stream.filter
		System.out.println("squares: " + map(xs, x -> x * x));  // Stream.map

		// System.out.println(fib1(50));
		System.out.println(fib2(0, 1, 50));
	}
}
