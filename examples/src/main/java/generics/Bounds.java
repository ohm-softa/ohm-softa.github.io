package generics;


import java.util.LinkedList;
import java.util.List;

public class Bounds {

	void augment(List<? super Number> list) {
		for (int i = 1; i <= 10; i++) {
			list.add(i);  // this works
			// !! list.add(new Object());
		}

		// !! Number k = list.iterator().next();  // compile time error: can't resolve type
		Number k = (Number) list.iterator().next();  // runtime hazard: ClassCastException
	}

	static <T extends Number> void includeIfEven(List<? super T> evens, T n) {
		if (n.intValue() % 2 == 0) {
			evens.add(n);
		}
	}

	public static void main(String[] args) {
		List<Number> nums = new LinkedList<>();
		List<Integer> ints = new LinkedList<>();
		List<Object> objs = new LinkedList<>();

		includeIfEven(nums, 3);
		// includeIfEven(ints, 2.0);
		includeIfEven(objs, 4);
	}
}
