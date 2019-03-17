package designpattern.singleton;

public class SingletonB {
	private static final SingletonB instance;

	static {
		// use static initializer block
		instance = new SingletonB();

		// do more fancy stuff
	}

	public static SingletonB getInstance() {
		return instance;
	}

	private SingletonB() {

	}
}
