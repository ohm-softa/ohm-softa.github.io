package fp;

import java.math.BigInteger;
import java.util.function.*;
import java.util.stream.Stream;

import static fp.List.empty;
import static fp.List.list;

public class App2 {

	// functions as first-class citizens
	static <T> List<T> filter(List<T> xs, Predicate<T> p) {
		if (xs.isEmpty()) return empty();
		else if (p.test(xs.head)) return list(xs.head, filter(xs.tail, p));
		else return filter(xs.tail, p);
	}

	static <T, R> List<R> map(List<T> xs, Function<T, R> f) {
		if (xs.isEmpty()) return empty();
		else return list(f.apply(xs.head), map(xs.tail, f));
	}

	static <T> void forEach(List<T> xs, Consumer<T> c) {
		if (xs.isEmpty()) return;
		else {
			c.accept(xs.head);
			forEach(xs.tail, c);
		}
	}

	// reduction

	static int sum(List<Integer> xs, int z) {
		if (xs.isEmpty()) return z;
		else return sum(xs.tail, z+xs.head);
	}

	static String join(List<String> xs, String z) {
		if (xs.isEmpty()) return z;
		else return join(xs.tail, z.concat(xs.head));
	}

//	static <T> T reduce(List<T> xs, T z) {
//		if (xs.isEmpty()) return z;
//		else return reduce(xs.tail, z + xs.head);
//	}

	static <T> T reduce(List<T> xs, T z, BinaryOperator<T> op) {
		if (xs.isEmpty()) return z;
		else return reduce(xs.tail, op.apply(z, xs.head), op);
	}

	static BigInteger reduce(List<Integer> xs, BigInteger z) {
		if (xs.isEmpty()) return z;
		else return reduce(xs.tail, z.add(BigInteger.valueOf(xs.head)));
	}

	static <T, R> R foldl(List<T> xs, R z, BiFunction<R, T, R> op) {
		if (xs.isEmpty()) return z;
		else return foldl(xs.tail, op.apply(z, xs.head), op);
	}

	// motivate foldr

	static BigInteger sum(List<Integer> xs, BigInteger z) {
		if (xs.isEmpty()) return z;
		else return BigInteger.valueOf(xs.head).add(sum(xs.tail, z));
	}

	static <T, R> R foldr(List<T> xs, R z, BiFunction<T, R, R> op) {
		if (xs.isEmpty()) return z;
		else return op.apply(xs.head, foldr(xs.tail, z, op));
	}

	// tail-recursive map
	static <T, R> List<R> maptr(List<T> xs, Function<T, R> op) {
		List<T> reverse = foldl(xs, empty(), (ys, y) -> list(y, ys));
		List<R> mapped = foldl(reverse, empty(), (ys, y) -> list(op.apply(y), ys));
		return mapped;
	}

	public static void main(String[] args) {
		List<Integer> xs = list(1, 3, 3, 7);
		System.out.println(sum(xs, 0));

		List<String> ys = list("a", "b", "c", "d");
		System.out.println(join(ys, ""));

		System.out.println(reduce(list(1, 3, 3, 7), 0, (i, j) -> Integer.sum(i, j)));
		System.out.println(reduce(list(1, 3, 3, 7), 0, Integer::sum));

		System.out.println(reduce(list("a", "b", "c", "d"), "", (a, b) -> a.concat(b)));  // abcd
		System.out.println(reduce(list("a", "b", "c", "d"), "", String::concat));

		reduce(list(1, 3, 3, 7), 0, (i, j) -> { System.out.println(j); return j; });

		System.out.println(reduce(xs, BigInteger.ZERO));
		System.out.println(foldl(xs, BigInteger.ZERO, (b, i) -> b.add(BigInteger.valueOf(i))));

		// reverse
		System.out.println(foldl(xs, List.<Integer>empty(), (zs, z) -> list(z, zs)));

		// reduce-right
		System.out.println(sum(xs, BigInteger.ZERO));
		System.out.println(foldr(xs, BigInteger.ZERO, (i, b) -> BigInteger.valueOf(i).add(b)));

		// append
		System.out.println(foldr(xs, List.<Integer>list(49), (z, zs) -> list(z, zs)));

		// map: squares
		System.out.println(foldr(xs, List.<Integer>empty(), (z, zs) -> list(z*z, zs)));

		// filter: lt 5
		System.out.println(foldr(xs, List.<Integer>empty(), (z, zs) -> {
			if (z < 5) return zs;
			else return list(z, zs);
		}));

		// tail-rec map
		System.out.println(foldl(foldl(xs, List.<Integer>empty(), (zs, z) -> list(z * z, zs)), List.<Integer>empty(), (zs, z) -> list(z, zs)));


		System.out.println(Stream.of(1, 3, 3, 7)
				.reduce(0, Integer::sum));

		System.out.println(Stream.of(1, 3, 3, 7)
				.reduce(BigInteger.ZERO, (bi, i) -> bi.add(BigInteger.valueOf(i)), (a, b) -> a.add(b)));
	}


}
