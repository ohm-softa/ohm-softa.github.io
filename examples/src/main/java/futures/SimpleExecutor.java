package futures;

import java.util.concurrent.ExecutionException;

public class SimpleExecutor implements Executor {
	@Override
	public <T> Future<T> async(Callable<T> task) {
		return new Future<T> () {
			Thread t;
			T result;
			ExecutionException e;

			// constructor block
			{
				t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							result = task.call();
						} catch (Exception ex) {
							e = new ExecutionException(ex);
						}
					}
				});

				t.start();
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				t.join();
				if (e != null)
					throw e;
				return result;
			}
		};
	}
}
