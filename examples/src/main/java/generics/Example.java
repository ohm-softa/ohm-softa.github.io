package generics;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Arrays;

class Example<T> {
	public static <T extends Number> void includeIfEven(List<? super T> list, T inst) {
		if (inst.intValue() % 2 == 0)
			list.add(inst);
	}

	public static void main(String[] args) {
		List<Number> li = new LinkedList<>();
		List<Integer> ilist = new LinkedList<>();
		List<Object> olist = new LinkedList<>();

		includeIfEven(olist, new Integer(2));
		includeIfEven(olist, new Double(2.0));

		System.out.println(li);
	}
}
