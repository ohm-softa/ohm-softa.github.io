package diamond;

public interface Right extends Top {
	default void method() {
		System.out.println("Right.method()");
	}
}
