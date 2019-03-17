package futures;

import java.util.concurrent.ExecutionException;

interface Executor {
	<T> Future<T> async(Callable<T> task);
}
