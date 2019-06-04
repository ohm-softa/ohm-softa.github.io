---
title: "Parallel and Asynchronous Processing"
permalink: /14ln-parallel-async/
---

# Parallel Processing

# Processes

Up to now, the programs we wrote were basically a single _process_ in which instructions were executed one-by-one, in the order they were written down.
The following code

```java
class MyProgram {
	String name;
	MyProgram(String name) {
		this.name = name;
		System.out.println("Created MyProgram: " + name);
	}
	void printNum(int n) {
		System.out.println(name + ": " + n);
	}
	public static void main(String[] args) {
		MyProgram mp = new MyProgram("Test");
		for (int i = 0; i < 3; i++)
			mp.printNum(i);
	}
}
```

would yield the following output:

```
Created MyProgram Test
Test: 0
Test: 1
Test: 2
```

Or expressed as a sequence diagram, with methods as columns:

![single-process](/assets/process.svg)

This is also the behavior you see when using the debugger and going through your program step-by-step, using the _step-into_ action.

A _process_ has a self-contained (and isolated) environment.
A Process has a complete and private set of run-time resources; in particular, each process has its own memory space.


# Threads

Consider this simple class that models a bean counter.
It receives a name and allocates a large array of numbers; a call to `.run()` sorts the data.

```java
class BeanCounter {
	private final String name;
	private final double[] data;
	BeanCounter(String name, int n) {
		this.name = name;
		this.data = new double [n];
	}

	public void run() {
		System.out.println(name + " is starting...");
		Arrays.sort(data);
		System.out.println(name + " is done!");
	}
}
```

And here is an example program, allocating two bean counters, have them work, and then print a good bye message:

```java
public static void main(String... args) {
	BeanCounter b1 = new BeanCounter("Bureaucrat 1", 10000);
	BeanCounter b2 = new BeanCounter("Bureaucrat 2", 1000);

	b1.run();
	b2.run();

	System.out.println("main() done!");
}
```

With the expected result:

```
Bureaucrat 1 is starting...
Bureaucrat 1 is done!
Bureaucrat 2 is starting...
Bureaucrat 2 is done!
main() done!
```

![bureaucrats-1](/assets/bureaucrats.svg)


To make the bean counters work in parallel, use the [`Thread` class](https://docs.oracle.com/javase/9/docs/api/java/lang/Thread.html).
It takes an instance of `Runnable` of which it will execute the `.run()` method _in a separate thread_, once the thread's `.start()` method is called.
First, modify the `BeanCounter` to implement the `Runnable` interface

```java
public class BeanCounter implements Runnable {
	// ...
}
```

and then pass instances to the `Thread` class constructor:

```java
public static void main(String[] args) {
	BeanCounter b1 = new BeanCounter("Bureaucrat 1", 10000);
	BeanCounter b2 = new BeanCounter("Bureaucrat 2", 1000);

	new Thread(b1).start();
	new Thread(b2).start();

	System.out.println("main() done!");
}
```

![bureaucrats-2](/assets/bureaucrats_001.svg)

This will likely produce output similar to this:

```
Bureaucrat 1 is starting...
main() done!
Bureaucrat 2 is starting...
Bureaucrat 1 is done!
Bureaucrat 2 is done!
```

As you can see, the three methods (`main`, `b1.run` and `b2.run`) were executed in parallel.
If you run the code on your machine, you may get a different order of the output; this is because only one thread can write to `System.out` at a time, and they may get to that point at different times depending on your number of CPUs etc.

Instead of providing a `Runnable` to the `Thread`, you can also extend the `Thread` class and overwrite the `run` method.

Threads are sometimes called _lightweight processes_; they exist inside processes and have shared resources.
This allows communication but at the same time introduces risks.

From your operating systems class, you should know that there are _user_ and _system_ (or _kernel_) level threads.
In Java, threads are technically _user level_, since you work with the `java.lang.Thread` API (and the JVM) rather than directly with the operating system.
For the JVM, however, threads are typically implemented as _kernel level_ threads, to get the best performance; the VM translates the threading instructions into operations native to your operating system.


### Examples

Multi-threaded programming is ubiquitous in modern applications:
- browser: loading multiple resources at a time using concurrent connections
- rendering multiple animations on a page/screen
- handling user interactions such as clicks or swipes
- sorting data using divide-and-conquer
- concurrent network, database and device connections
- ability to control (pause, abort) certain long-lasting processes


# Synchronization

## Joining

The example above has one major flaw: the `main()` routine finishes before the actual work is done.
Transferred to the real world, this would mean that you delegate the work to your team, but immediately report that all work is done while your team is still working hard!

One (terrible) way to fix this is to _actively wait_ until a thread is done by checking its `.isAlive()` method.
Consider this simple example where our `Runnable` will sleep for 15 seconds (and do nothing):

```java
public class Joining implements Runnable {
	@Override
	public void run() {
		System.out.println("Sleeping for 15 seconds");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Thread t = new Thread(new Joining());
		t.start();

		while (t.isAlive())
			;  // do nothing, but really fast...

		System.out.println("Done!");
	}
}
```

While this works, this is a terrible idea: the check for `isAlive()` is really fast, thus the thread executing `main` will run "hot" without doing anything useful.

The correct way to solve this is to use the `join()` method of `Thread`:

```java
public static void main(String[] args) throws InterruptedException {
	Thread t = new Thread(new Joining());
	t.start();

	t.join();  // block/sleep until t is done
	System.out.println("Done!");
}
```

Note that `join()` (and `sleep()`) may throw an `InterruptedException` which needs to be appropriately handled.

> You can use `join()` whereever you have access to the reference of a thread.
> For example, you could give one thread the reference to another thread, to have it start after the other thread finished.


## Shared Resources

But let's get back to the example with the bean counters.
Imagine you have a number of bean counters in your team, and they should all add to the same counter.
This is possible, since threads share the memory:

```java
class Counter {
	private int c = 0;
	int getCount() { 
		return c; 
	}
	void increment() {
		c = c + 1;
	}
}
```
```java
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
}
```

So each `TeamBeanCounter` receives a reference to the shared counter, and in the `run()` method, they each increment the counter by one; once they did this 100000 times, they print the total count.

Thus, this program:

```java
public static void main(String[] args) {
	Counter c = new Counter();

	new Thread(new TeamBeanCounter(c)).start();
	new Thread(new TeamBeanCounter(c)).start();
	new Thread(new TeamBeanCounter(c)).start();
	new Thread(new TeamBeanCounter(c)).start();
}
```

Should output something where the last line (i.e. the last bean counter to finish) reads

```
Total beans: 400000
```

However, you will more likely read something along the lines of:

```
Total beans: 362537
```

So what happened?
All bean counters share the same `Counter` instance; however, they since each thread executes its own methods, we need to look at the `increment()` method carefully:

```java
void increment() {
	c = c + 1;
}
```

In fact, to execute this assignment, the JVM needs to first load the value of `c`, then add `1`, and then assign it back to `c`.

```java
void increment() {
	int tmp = c;
	++tmp;
	c = tmp;
}
```

That means, two threads can be at different steps of this method while they share the memory.
Note that the _stack_ variables are per-thread, whereas the _heap_ variables (the counter instance) are shared (see [Java VM Specifications](https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-2.html#jvms-2.5.2)).


|   | Thread 1 | Thread 2 | _result_ |
| - | -------- | -------- | -------- |
| 1 | tmp1 = c |          | tmp1 = 0 |
| 2 |          | tmp2 = c | tmp2 = 0 |
| 3 | ++tmp1   |          | tmp1 = 1 |
| 4 |          | ++tmp2   | tmp2 = 1 |
| 5 | c = tmp1 |          | c = 1  |
| 6 |          | c = tmp2 | **c = 1 !** |
{: .table }

Since the _memory_ is shared, both threads retrieve the same value `c` of the counter (at the beginning: 0).
Then they each increment that value by one, before they individually assign the shared `c` -- with their own values!

To fix this, we need to tell the JVM, which code segment may only entered _by one thread at a time_; this is also called _locking_.
This is done using the keyword `synchronized`, either as a modifier to the method or as a block instruction.

```java
synchronized void increment() {
	c = c + 1;
}
```
```java
void increment() {
	synchronized (this) {
		c = c + 1;
	}
}
```

Each thread trying to enter the _critical section_ first has to acquire the lock (here: `this`), or wait for it to become available.
The thread then executes all statements inside the synchronized section, before it releases the lock again.

Note that for the block instruction, the argument to `synchronized` defines the _lock_ object; this could be any object, and is implicitly `this` when using `synchronized` as a method modifier.
Using either method, the output of the `TeamBeanCounter` program is as expected:

```
Total beans: 400000
```

## Synchronized Methods vs. Lock Objects

- The benefit of `synchronized` methods is, that it is an easy fix to existing code; the downside is that the whole method is locked.
- The benefit of `synchronized (lock) { ... }` is that the locking can be applied to very few (but critical) operations; note that in the above example, a `synchronized (this)` spanning the whole method body is effectively the same as making the method `synchronized`.
- While you can use any object as lock, you must use the the same reference in all relevant places.


# Communication

The `synchronized` keyword allows us to safely change values which will be read by other threads.
However sometimes this basic mechanism is not enough.
Consider the following scenario:

![Deadlock](/assets/threads-deadlock.svg)

This is a classic _deadlock_, similar to a Mexican stand-off from the movies:
`threadA` wants to acquire `lock2`, which is currently taken by `threadB`; `threadB` wants `lock1`, which is currently taken by `threadA`.
As a result, nothing happens, the situation is stuck.

When inside a `synchronized` section, you can use the methods `wait()`, `notify()` and `notifyAll()` of the lock to _wait_ in a thread until _notified_ by some other thread.
You can use this mechanism to have workers only be active when they should be:

![threads-wait-notify](/assets/threads-wait-notify.svg)

> Note: These methods are parts of the Java threading API and are defined `final` in `Object`

> Note: `notify()` will wake up one (random) other thread; `notifyAll()` will wake up _all_ other waiting threads.


## The Consumer-Producer Problem

The classic example to demonstrate how to use `wait()` and `notify()` in combination is the _consumer-producer-problem_: producers store data in a (ring) buffer and consumers take data out of the buffer.
A typical example is a streaming media player: the producer is the decoder that reads encoded data and stores decoded (raw) media in the buffer; the media device (e.g. video player) is the consumer, removing ready-to-render data from the buffer.

![consumer-producer](/assets/consumer-producer.png)

The buffer provides the basic operations `put(T t)` and `T get()`, which should each block if full (put) or empty (get).

```java
class Buffer<T> {
	List<T> buffer = new LinkedList<>();
	final int max = 10;

	synchronized void put(T obj) throws InterruptedException {
		// wait until buffer not full
		while (buffer.size() == 10)
			wait();

		buffer.add(obj);

		// wake up other threads waiting for buffer to change
		notifyAll();
	}

	synchronized T get() throws InterruptedException {
		// wait until there's something in the buffer
		while (buffer.size() == 0)
			wait();

		T obj = buffer.remove(0);
		
		// wake up other threads waiting for buffer to change
		notifyAll();
		return obj;
	}
}
```

Now you could have several threads of producers and consumers which call the `get` and `put` methods.
If somebody calls `get` on an empty buffer, it will repeatedly `wait` until there is something in the buffer.
If somebody calls `put` on a full buffer, it will repeatedly `wait` until there is space available.

This works because **only one** thread at a time is allowed within the critical section; this is also the case why `wait` and `notify` can only be called **within a critical section**.


# Thread Lifecycle

And here is the complete lifecycle of the thread showing the effects of `start()`, `wait()`, `sleep()` and `join()`.

![thread-lifecycle](/assets/thread-lifecycle.svg)


# Advanced Aspects of Java: Atomic Access

In Java, reads and writes of references and most basic types are _atomic_, i.e. they happen effectively at once.
This does **not** hold for `long` and `double`, where the reads and writes are done in two chunks of 32 bit.
Thus, two competing threads can read/write corrupted/incomplete data.

Use the `volatile` keyword to make any variable read/write atomic.
In certain cases, this may already be enough to synchronize your threads, i.e. a `synchronized` might not be required anymore, since any change on a volatile variable is immediately visible to other threads.
However, `volatile` does not extend to the _operators_, i.e. a increment or decrement operator may still require synchronization.

Final (`final`) fields however, are [thread-safe](https://docs.oracle.com/javase/specs/jls/se9/html/jls-17.html#jls-17.5), since their values don't change.


# Further Reading

For more details and examples, refer to the chapter [Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/) of the Oracle Java SE Tutorial.
[Chapter 17 of the Java Language Specification](https://docs.oracle.com/javase/specs/jls/se9/html/jls-17.html) describes the full specifics of Java's threads and locks.

---

# Working Asynchronously

In the examples above, threads are used to distribute work to multiple CPUs in order to speed up processing, such as sorting data or media encoding/decoding.
In modern application design, threads are used to take workload off the main thread so that the actual application (e.g. a GUI or microservice) remains responsive to external input.

Recall how to use threads (in Java):

1. Extend `Thread` and overwrite `run()`, or create a new thread with reference to a `Runnable`
2. Call `start()` on the thread instance to signal that the thread is ready to run.
3. Optionally use `join()` on the thread instance to wait for its completion.

```java
Thread t = new Thread(new Runnable() {
	@Override
	public void run() {
		System.out.println("Hello from my custom thread!");
	}
});

t.start();
System.out.println("Hello from the application main thread!")

System.out.println("Waiting for thread to complete...");
t.join();

System.out.println("All done.");
```

From the main application's point of view, the code in `run()` is run _asynchronously_: The execution of the `main` method continues right after the `t.start()` call, independent of the execution of `run()`.
On a multi-core computer, the threads may actually be active at the same time, one thread per core.

There are two major drawbacks of this method: 

1. There is no (direct) way to provide arguments to or obtaining _results_ from the computation done in the thread.
	This is also reflected in the fact that `run()` does not take any arguments and has return type `void`.
2. There is no (direct) way to communicate potential exceptions that occur in the thread to the main application; the signature of `run()` can not be modified.

How could these common issues be solved?

A possible solution is to store both arguments and results inside the thread or `Runnable`, and relay possible exceptions to whoever tries to retrieve the result.

```java
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

	int getResult() throws Exception {
		if (e != null)
			throw e;

		return result;
	}
}
```

Which can be used in your main program:

```java
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
```


## The Future of Callables

The code above features three key parts.

1. A `Runnable` which may use external information and stores results as attributes.
	Possible exceptions raised in the `run()` method are caught and stored as attribute.
2. The main program creates and starts the thread to execute the `run()` method of the above instance.
3. The main program uses the thread's `join()` to wait for completion, and our modified `Runnable`'s `get()` to obtain the result (or raise a possible exception).


Let's refactor the code to separate out the recurring scheme (the mechanics) from the actual business logic (the contents of `run`).

For (1), we'll use a different interface to reflect a return type.

```java
interface Callable<V> {
	V call();
}
```

For (3), we'll introduce an interface that will allow us to retrieve the _future result_ from the `Callable`, once it's done.

```java
interface Future<T> {
	T get();
}
```

The remaining part (2) that organizes the thread logistics to run the actual task, we'll stick here:

```java
interface Executor {
	<T> Future<T> async(Callable<T> task);
}
```

As you can see, the `Executor` ties things together.
Let's see how we can make the threading work behind the scenes.
The incoming `Callable` needs to be wrapped into a thread, while watching for possible exceptions.
Furthermore, it must return a `Future` that, on `get()`, waits for the thread to finish and then either throws any exception that occurred or returns the result.

```java
class SimpleExecutor implements Executor {
	@Override
	public <T> Future<T> async(Callable<T> task) {
		// create anonymous Future instance
		return new Future<T> () {
			Thread t;  // handle on the thread (get needs to wait!)
			T result;  // save the result
			ExecutionException e;  // in case something goes wrong?

			// constructor block
			{
				// create a new thread and start it
				// the runnable "watches" over the task
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
				// wait for it...
				t.join();

				// anything fishy?
				if (e != null)
					throw e;

				// all good, return result!
				return result;
			}
		};
	}
}
```

Putting the pieces together, the `SimpleExecutor` simplifies asynchronous execution tremendously:

```java
Executor ex = new SimpleExecutor();
int a = 4, b = 0;
Future<Integer> f1 = ex.async(new Callable<Integer>() {
	@Override
	public Integer call() {
		// can use variables from outer scope
		return a / b;
	}
});

// do other things if you like...

try {
	System.out.println(f2.get());
} catch (ExecutionException e) {
	System.out.println("The thread raised an exception: "
		+ e.getCause());
}
```


## Callables, Futures and Executors since Java 5

Since Java 5, these three parts are realized as [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html) (1), [Executor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html) (2) and [Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html) (3).
The "real" interfaces are slightly different to allow a more faceted use.

```java
interface Callable<V> {
	V call();
}
```
```java
interface Future<V> {
	boolean cancel(boolean mayInterruptIfRunning);
	V get();
	V get(long timeout, TimeUnit unit);
	boolean isCancelled();
	boolean isDone();
}
```
```java
interface ExecutorService extends Executor {
	void execute(Runnable command);  // for convenience
	<T> Future<T> submit(Callable<T> task);
	Future<?> submit(Runnable task);
	<T> Future<T> submit(Runnable task, T result);
	// ...
}
```

Java 5 also introduced a [number of different executors](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html), here are a few examples:

```java
Executor executor = Executors.newSingleThreadExecutor();

// executor = Executors.newCachedThreadPool();  // reuses threads
// executor = Executors.newFixedThreadPool(5);  // use 5 threads

executor.execute(new Runnable() { 
	public void run() {
		System.out.println("Hello world!");
	}
}); 
```


# Asynchronous on Steroids

The previously introduced `Executor` allows to asynchronously execute a `Callable`, only waiting (blocking) on the executing thread when calling `get()` on the `Future`.

This is convenient enough for basic use cases (e.g. re-query the database after a swipe-down gesture on mobile), but quickly reaches its limits for more complex operations, where the result of one operation gets processed by another.

For the remainder of this chapter, let's consider this rather frequent example: 
For a cloud-backed application, you write a `displayStatus()` method to 
- sign in with username and password; on success you gain a token to 
- retrieve the user's status; once received,
- greet the user.


```java
public class Workflow {
	static void displayStatus() throws ExecutionException, InterruptedException {
		final String user = "riko493";
		final String pass = "12345";  // spaceballs, anyone? :-)

		// log in...
		Future<String> f1 = Executor.async(new Callable<String>() {
			public String call() {
				System.out.println("Authenticating with " + user + ":" + pass);
				return "secrettoken";
			}
		});

		final String token = f1.get();

		// retrieve user
		Future<String> f2 = Executor.async(new Callable<String>() {
			public String call() {
				System.out.println("Retrieving user details with token " + token);
				return "lightly sleep deprived, should get haircut";
			}
		});

		final String details = f2.get();

		System.out.println("Welcome " + user + "! You look " + details);
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		displayStatus();  // blocks until completed!!
	}
}
```

While this works, the `displayStatus()` method blocks until the status is displayed, thus blocking the remainder of the application from being responsive.
Also, reacting to execution exceptions becomes tricky: which parts should be guarded by `try-catch`?

It would be desirable to be able to chain asynchronous actions that depend on each other.
The [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) class added in Java 8 provides exactly that:

```java
class CompletableFuture<T> implements CompletionStage<T>, Future<T> {
	static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) { 
		// ...
	}
	<U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		// ...
	}
	<U> CompletionStage<U> thenAcceptAsync(Consumer<? super T> action) { 
		// ...
	}
	CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
		// ...
	}
	// and much more...
}
```

The `supplyAsync` accepts a `Supplier<U>`, which is almost identical to `Callable<T>`, but rooted in `java.util.function` for semantic reasons.
This method is used to create a `CompletableFuture` that can then be chained with other actions.

Note the generic typing of `thenApply` and `thenAccept`: Although the `CompletableFuture` is generic in `T`, both methods return `CompletionStage<U>`, i.e. a (possibly) different generic type.

The `thenApply` method takes a `@FunctionalInterface` [Function](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html) to map values of `? super T` to `? extends U`.

The `thenAccept` method takes a `@FunctionalInterface` [Consumer](https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html) to act on `? super T`.

The `exceptionally` method accepts a function that maps a `Throwable` to `? extends T` to handle exceptions gracefully by providing alternate input to the next in line.

Since all those are functional interfaces, we can use method references and lambda notation for a cleaner codebase.

```java
CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> "riko493:12345")
	.thenApplyAsync(creds -> {
		System.out.println("Authenticating with " + creds);
		return "secrettoken";
	})
	.thenApplyAsync(token -> {
		System.out.println("Retrieving status with token=" + token);
		return "in the mood for holidays";
	})
	.thenAccept(status -> System.out.println(status))
	.exceptionally(ex -> { System.out.println("Oops, something went wrong: " + ex); return null; });

System.out.println("All done!");
```

You can even combine (synchronize) multiple `CompletableFuture` by using its

```java
public <U,V> CompletableFuture<V> thenCombineAsync(
	CompletionStage<? extends U> other,
	BiFunction<? super T,? super U,? extends V> fn)
```


# Other Concurrency Utilities

Since Java 5, there are a number of other concurrency utilities available.

- `java.util.concurrent` provides thread safe collections
	* `BlockingQueue`: _FIFO_ queue that blocks on write-if-full and read-if-empty
	* `ConcurrentMap`: map with atomic read/write operations
- `ThreadLocalRandom` provides random numbers with per-thread randomization
- `AtomicInteger` allows atomic integer increment/decrement
- `CountDownLatch` allows for atomic counting and waiting


A frequent use for the `CountDownLatch` is batch processing across multiple threads:

```java
List<String> filesToProcess = ...;

CountDownLatch latch = new CountDownLatch(filesToProcess.size());
ExecutorService ex = Executors.newFixedThreadPool(5);

for (String f : filesToProcess) {
	ex.submit(() -> {
		System.out.println("processing file " + f);

		// countdown when done!
		latch.countDown();
	});
}

latch.await();

// shutdown the threads to allow main() to quit.
ex.shutdown();
```

<p style="text-align: right">&#8718;</p>
