package futures;

import java.util.concurrent.ExecutionException;

interface Future<T> {
	T get() throws InterruptedException, ExecutionException;
}
