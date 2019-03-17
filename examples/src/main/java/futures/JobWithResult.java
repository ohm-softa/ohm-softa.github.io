package futures;

public class JobWithResult implements Runnable {
	private int a, b, result;
	private Exception e;

	JobWithResult(int a, int b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public void run() {
		try {
			this.result = a / b;
		} catch (Exception e) {
			System.out.println("Oops, an exception raised; storing for later.");
			this.e = e;
		}
	}

	public int getResult() throws Exception {
		if (e != null)
			throw e;

		return result;
	}

	public static void main(String... args) throws InterruptedException {

		JobWithResult jwr = new JobWithResult(4, 0);

		Thread t = new Thread(jwr);

		t.start();

		System.out.println("Waiting for thread to complete...");
		t.join();

		System.out.println("All done.");
		try {
			System.out.println(jwr.getResult());
		} catch (Exception e) {
			System.out.println("Ooops: " + e.toString());
		}

	}
}
