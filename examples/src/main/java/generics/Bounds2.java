package generics;

import java.util.LinkedList;
import java.util.List;

public class Bounds2 {
	public static void main(String[] args) {
		B b1 = null;

		List<A> l2 = new LinkedList<>();

		l2.add(new B());
		l2.add(new D());
	}

	// class A
	// class B extends A
	// class Z

	// which classes ("?") are "above" A? A, Object; but not B, D or Z
	static void bounds1(List<? extends A> li) {
		for (A a : li) {
			// ...
		}


		for (int i = 0; i < li.size(); i++) {
			Object o = li.get(i);  // thanks, Java.
			A a = li.get(i);
			// !! B b = li.get(i);
			// !! Z z = li.get(i);
		}

	}

	// which classes are "below" A? A, B, D
	static void bounds2(List<? super A> li) {
		// !! li.add(new Object());
		li.add(new A());
		li.add(new B());
		// !! li.add(new Z());

		// works always...
		Object o = li.get(0);
		// !! A a = li.get(0);  // could also be B!
	}
}
