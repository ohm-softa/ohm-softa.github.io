package diamond;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bottom extends Klass implements Left, Right {
	public void method() {
		System.out.println("Bottom.method()");

		// this his how to access regular base class implementations
		super.method();

		// if you need defaults of interfaces
		Left.super.method();
		Right.super.method();
	}

	final public static void main(String[] args) {
		Bottom b = new Bottom();
		b.method();
	}
}
