package diamond;

public interface Left extends Top {
	default void method() {
		System.out.println("Left.method()");
	}
}
