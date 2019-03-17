package threads;

public class TeamBeanCounter implements Runnable {
	Counter c;
	TeamBeanCounter(Counter c) {
		this.c = c;
	}

	@Override
	public void run() {
		for (int i = 0; i < 100000; i++) {
			c.increment();
		}
		System.out.println("Total beans: " + c.getCount());
	}

	public static void main(String[] args) {
		Counter c = new Counter();

		new Thread(new TeamBeanCounter(c)).start();
		new Thread(new TeamBeanCounter(c)).start();
		new Thread(new TeamBeanCounter(c)).start();
		new Thread(new TeamBeanCounter(c)).start();
	}
}
