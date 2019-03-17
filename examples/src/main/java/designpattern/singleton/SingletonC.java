package designpattern.singleton;

public class SingletonC {
	private static SingletonC instance;

	// lazy instantiation
	public static SingletonC getInstance() {
		if (instance == null)
			instance = new SingletonC();

		return instance;
	}

	private SingletonC() {

	}
}
