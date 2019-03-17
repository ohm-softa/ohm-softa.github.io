package threads;

public class Joining implements Runnable {
	@Override
	public void run() {
		System.out.println("Sleeping for 15 seconds");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done sleeping");
	}

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Joining());
		t.start();

//		while (t.isAlive())
//			;

//		t.join();

		System.out.println("Done!");
	}
}
