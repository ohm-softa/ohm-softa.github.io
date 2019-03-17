package threads;

import java.util.Arrays;

class BeanCounter implements Runnable {
	private final String name;
	private final double[] data;
	BeanCounter(String name, int n) {
		this.name = name;
		this.data = new double [n];
	}

	@Override
	public void run() {
		System.out.println(name + " is starting...");
		Arrays.sort(data);
		System.out.println(name + " is done!");
	}

	public static void main(String[] args) throws InterruptedException {
		BeanCounter b1 = new BeanCounter("Bureaucrat 1", 10000);
		BeanCounter b2 = new BeanCounter("Bureaucrat 2", 1000);

//		b1.run();
//		b2.run();

		Thread t1 = new Thread(b1);
		Thread t2 = new Thread(b2);

		t1.start();
		t2.start();

		while (t1.isAlive() || t2.isAlive())
			;

//		t1.join();
//		t2.join();

		System.out.println("main() done!");
	}
}
