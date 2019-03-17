package diamond;

public interface Top {
	default void method() {
		System.out.println("Top.method()");
	}
}
