package futures;

import java.util.concurrent.ExecutionException;

public class Task {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		Executor ex = new SimpleExecutor();
		int a = 4, b = 0;
		Future<Integer> f1 = ex.async(new Callable<Integer>() {
			@Override
			public Integer call() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// can use variables from outer scope
				return a + b;
			}
		});

		System.out.println(f1.get());

		Future<Integer> f2 = ex.async(new Callable<Integer>() {
			@Override
			public Integer call() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return a / b;
			}
		});

		try {
			System.out.println(f2.get());
		} catch (ExecutionException e) {
			System.out.println("There was an exception raised in the thread: " + e.getCause());
		}
	}
}
