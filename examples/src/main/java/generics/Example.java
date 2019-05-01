package generics;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Arrays;

class Example<T> {
	public static <T extends Number> void includeIfEven(List<? super T> list, T inst) {
		if (inst.intValue() % 2 == 0)
			list.add(inst);
	}

	public static <T> void bounds(List<? extends T> prod, List<T> prodcons, List<? super T> cons) {
	}

	public static <T> void bounds(List<? extends T> prod, T meh, List<? super T> cons) {
	}

	public static void main(String[] args) {
		List<Number> nums = new LinkedList<>();
		List<Integer> ints = new LinkedList<>();
		List<Object> objs = new LinkedList<>();

		includeIfEven(objs, 2);
		includeIfEven(objs, 2.0);

		System.out.println(objs);

		List<A> as = new LinkedList<>();
		List<B> bs = new LinkedList<>();
		List<D> ds = new LinkedList<>();

		bounds(ds, bs, as);
		bounds(ds, new B(), as);
		Example.<B>bounds(ds, new B(), as);
	}
}
