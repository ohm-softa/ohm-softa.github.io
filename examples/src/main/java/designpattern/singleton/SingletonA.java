package designpattern.singleton;

public class SingletonA {
	public static final SingletonA instance = new SingletonA();

	private SingletonA() {
		// this prevents instantiation -- no-one can see the constructor
	}
}
