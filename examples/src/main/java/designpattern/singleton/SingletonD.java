package designpattern.singleton;

public enum SingletonD {
	INSTANCE;

	// add more private fields; will not be serialized!
	int val = 0;

	public int doSomething() {
		return val++;
	}

	public static void main(String... args) {
		System.out.println(SingletonD.INSTANCE.doSomething());
		System.out.println(SingletonD.INSTANCE.doSomething());
		System.out.println(SingletonD.INSTANCE.doSomething());
	}
}
