package diamond;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bottom extends Klass implements Left, Right {
	public void method() {
		super.method();
		Left.super.method();
		Right.super.method();
	}

	final public static void main(String[] args) {
		Bottom b = new Bottom();
		b.method();
	}
}
