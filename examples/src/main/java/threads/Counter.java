package threads;

class Counter {
	private int c = 0;
	int getCount() {
		return c;
	}
	void increment() {
		c = c + 1;
	}
	void incrementA() {
		synchronized (this) {
			c = c + 1;
		}
	}
	synchronized void incrementB() {
		c = c + 1;
	}
}
