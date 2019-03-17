---
title: "Introduction to Functional Programming"
permalink: /12ln-fp1/
---

# Functional Programming

[Functional Programming](https://en.wikipedia.org/wiki/Functional_programming) (FP) is a programming paradigm that (re-)gained quite some traction in the past years.
One of the most populuar functional programming languages these days is [Scala](http://scala-lang.org/), which combines object-oriented and functional aspects with a very neat syntax and high portability: it is executed on the Java VM and can thus integrate seamlessly with any existing Java libraries.

> If you prefer reading a text book, I can recommend [Functional Programming in Java](https://www.amazon.de/Functional-Programming-Java-Harnessing-Expressions/dp/1937785467/) by Venkat Subramaniam.


### Detour: The Beautiful Syntax of Scala

Scala has a beautiful syntax:
- It follows the substitution principle, where the result of the last instruction is the return value.
- It has built-in operators for list operations (add, split, etc.)

With scala, _insertion sort_ can be written in just a few lines of code:

```scala
// to sort a list...
def isort(xs: List[Int]): List[Int] = xs match {
	// an empty list is sorted
	case Nil => Nil
	// a list with a single element is also sorted
	case List(x) => List(x)
	// otherwise, cut off the first element (y) and
	// insert it into the sorted remaining list (ys)
	case y :: ys => insert(isort(ys), y)
}

// to insert an element into a (sorted) list...
def insert(xs: List[Int], x: Int): List[Int] = xs match {
	// if the list was empty, return a new list with just x
	case Nil => List(x)
	// otherwise: cut off the first element of xs and ...
	case y :: ys =>
		if (x < y) x :: xs       // prepend x to xs
		else y :: insert(ys, x)  // insert x into ys
}
```

But back to FP in _Java_.

(Pure) FP is based on two principles: **Immutability** (all objects are unchangeable) and **functions as first-class citizens** (functions are objects and can thus be passed as arguments).

_Immutability_ is a concept already found in the Java language.
For example, `String`s are immutable since there is no method to change their content, and variables can be declared `final`.

> nota bene: `final` on method signatures indicate that a method cannot be overwritten anymore.

Java 8 added syntactical sugar to make the language more functional: _lambda expressions_ and _functional interfaces_ (`@FunctionalInterface`).
As you know, everything (aside from primitive types) in Java is an `Object`.
`@FunctionalInterface` is an annotation to interfaces indicating that there is exactly one non-default method to be implemented.
Anonymous instances to functional interfaces can be written as lambda expressions:

```java
@FunctionalInterface
interface Function<A, B> {
	B apply(A obj);
}
```
```java
Function<Integer, Integer> square1 = new Function<Integer, Integer>() {
	@Override
	public Integer apply(Integer i) {
		return i * i;
	}
}

// or shorter as lambda expression (arglist) -> { block; }
Function<Integer, Integer> square2 = (Integer i) -> { return i * i };

// or even shorter, for single instructions
Function<Integer, Integer> square3 = i -> i * i;
```

> The types are usually automatically inferred.
> For single instructions, you can omit the curly braces and `return`.


So what's the big deal with functional programming?

1. Since objects are immutable, parallization is (almost) trivial (you may have heard of [map-reduce](https://de.wikipedia.org/wiki/MapReduce)).
2. _Separation of Concerns (SoC)_: FP helps you to separate the _data traversal_ (how you _iterate_ the data) from the _business logic_ (what you _do_ with the data).

Let me illustrate this with a simple example (will go through it in detail at the end of the chapter).
Say you want to
- retrieve all students from a database,
- filter out those who took _Programmieren 3_,
- load (all of) their transcript of records from another database
- print all class names

```java
// code in src/fplive/{Database,Student,Transcript,Record}.java
// data in src/resources/{students,tors}.json
for (Student s : Database.getStudents()) {
	if (s.getClasses().contains("Programmieren 3")) {
		Transcript tr = Database.getToR(s.getMatrikel());
		for (Record r : tr)
			System.out.println(r);
	}
}
```

As you can see, the traversal logic is closly tied with the business logic.
With functional programming, we will be able to formulate this in a much cleaner way, so stay tuned!


# Immutability

Java's built-in `java.lang.String` class is _immutable_: there is no way to change the contents of a `String` object once it has been assigned.

For the remainder of this (and the [next](/13ln-fp2/)) chapter, let's consider a new (generic) immutable list class:

```java
class List<T> {
	final T head;
	final List<T> tail;

	private List(T el, List<T> tail) {
		this.head = el;
		this.tail = tail;
	}

	boolean isEmpty() {
		return head == null;
	}

	// factory methods for convenience...
	static <T> List<T> empty() {
		return new List<T>(null, null);
	}

	static <T> List<T> list(T elem, List<T> xs) {
		return new List<>(elem, xs);
	}
	
	static <T> List<T> list(T... elements) {
		if (elements.length == 0)
			return empty();
		int i = elements.length - 1;
		List<T> xs = list(elements[i], empty());
		while (--i >= 0)
			xs = list(elements[i], xs);
		return xs;
	}
}
```

Here's an example usage:

```java
import static List.empty;
import static List.list;

List<Integer> sequence = list(1, 2, 3, 4, 5);
List<Integer> emptyList = empty();
List<Integer> prepend = list(0, empty());

System.out.println(sequence.isEmpty());   // "false"
System.out.println(emptyList.isEmpty());  // "true"
System.out.println(prepend.isEmpty());    // "false"
```

By now, you probably already realized the main issue with this class: once it's initialized, there is no way to change it.
That means: all mutations on this list will have to create a _new_ list.

Even "worse": if variables can't be changed, there is no `for`/`while` iteration!

This brings us right to the techique central to functional programming:


# Recursion

> To understand recursion, you first must understand recursion.

(Even [Google knows that](https://www.google.com/search?q=recursion)!)

To warm up, let's formulate a recursive `toString()` method for our `List` class.
**Remember:** When writing recursive functions, you ned to make sure to capture the _terminal_ cases (the ones where you know the answer) and the _recursion cases_ (the ones where you make the recursive calls).
For convenience, we'll make this a member function:

```java
class List<T> {
	// ...

	/**
	 * Either 'nil' if the list is empty, or 
	 * '( head tail.toString )' otherwise
	 */
	@Override
	public String toString() {
		if (isEmpty()) return "nil";
		else return "(" + head + " " + tail + ")";
	}
}
```

```java
System.out.println(list(7, 3, 1, 3));  // "(7 (3 (1 (3 nil))))"
```

> Note: For the remainder of this chapter, there is absolutely no `for`/`while`, only `if` statements and recursive calls.


## Simple Recursion

Similarly, we can formulate a `contains` method that checks if a list contains an element: Either the `head` matches, or it may be contained in `tail`.

```java
static <T> boolean contains(List<T> xs, T obj) {
	if (xs.isEmpty()) return false;
	else if (xs.head.equals(obj)) return true;
	else return contains(xs.tail, obj);
}
```

The same with the length of a list: An empty list has length zero, any other list is one plus the length of its tail.

```java
static <T> int length(List<T> xs) {
	if (xs.isEmpty()) return 0;
	else return 1 + length(xs.tail);
}
```


## Recursion with List Generation

Things become a bit more tricky if we want to mutate lists (or more precicely: 
	get new lists which differ from the old ones).
For example, consider the `take(int i)` and `drop(int i)` functions that return a list with the first `i` or the sublist following the `i`-th element, respectively.


```java
// recursion with list generation
static <T> List<T> take(List<T> xs, int n) {
	if (n <= 0 || xs.isEmpty()) return empty();
	else return list(xs.head, take(xs.tail, n-1));
}

static <T> List<T> drop(List<T> xs, int n) {
	if (n <= 0 || xs.isEmpty()) return xs;
	else return drop(xs.tail, n-1);
}
```

Appending to a list recursively is actually similar to the iterative way: 
	if the target list is empty, the new list is the appendix;
	otherwise we make a new list where we keep the head but append to the tail.

```java
static <T> List<T> append(List<T> xs, List<T> y) {
	if (xs.isEmpty()) return y;
	else return list(xs.head, append(xs.tail, y));
}
```

Since we know how to append to a list, list reversal becomes trivial:
	just make a new list of the current head, and append that to the reversal of the tail.

```java
static <T> List<T> reverse(List<T> xs) {
	if (xs.isEmpty()) return xs;
	else return append(reverse(xs.tail), list(xs.head, empty()));
}
```


## Recursive Sort Algorithms

Some sort algorithms can be formulated particularly simple using recursion.


### Insertion Sort

The idea of [insertion sort](https://en.wikipedia.org/wiki/Insertion_sort) is that _inserting_ an element `x` into an already sorted list `xs` trivial:
	Skip all elements smaller then `x` before inserting.
In our immutable list scenario this means:
	"Copy" all values while smaller then `x`, then insert `x` and append the remaining list.
The actual insertion sort method then just inserts the head into the sorted remaining list.

```java
static <T extends Comparable<T>> List<T> isort(List<T> xs) {
	if (xs.isEmpty()) return xs;
	else return insert(xs.head, isort(xs.tail));
}

private static <T extends Comparable<T>> List<T> insert(T x, List<T> xs) {
	if (xs.isEmpty()) return list(x, empty());
	else {
		if (x.compareTo(xs.head) < 0) return list(x, xs);
		else return list(xs.head, insert(x, xs.tail));
	}
}
```


### Merge Sort

[Merge sort](https://en.wikipedia.org/wiki/Merge_sort) is a _divide-and-conquer_ algorithm where the key idea is that _merging_ two already sorted lists is trivial:
	Keep adding the smaller of both lists to your result list until all items have been added.
The actual merge sort method then (recursively) splits the input lists into halves until they only contain a single element or none at all -- those are already sorted.

```java
static <T extends Comparable<T>> List<T> msort(List<T> xs) {
	if (xs.isEmpty()) return xs;            // no element at all
	else if (xs.tail.isEmpty()) return xs;  // only single element
	else {
		int n = length(xs);
		List<T> a = take(xs, n/2);
		List<T> b = drop(xs, n/2);

		return merge(msort(a), msort(b));
	}
}

private static <T extends Comparable<T>> List<T> merge(List<T> xs, List<T> ys) {
	if (xs.isEmpty()) return ys;
	else if (ys.isEmpty()) return xs;
	else {
		if (xs.head.compareTo(ys.head) < 0)
			return list(xs.head, merge(xs.tail, ys));
		else
			return list(ys.head, merge(xs, ys.tail));
	}
}
```


## Detour: Tail Recursion

Let's take another short detour.

If you worked out all the above examples yourself, you probably got intimately familiar with `StackOverflowError` ([link](https://docs.oracle.com/javase/9/docs/api/java/lang/StackOverflowError.html)) -- the "infinite loop" of recursion.
The reason is that you had that many recursive calls that you ran out of stack memory to store your call data (arguments, variables, etc.).
This is also why some (less informed) programmers call recursive (and to that extent: functional) programming "inefficient".
They are right: if done wrong, recursive programming can be very inefficient.
Consider for example recursive definition of the [Fibonnaci numbers](https://en.wikipedia.org/wiki/Fibonacci_number):

```java
long fib(int i) {
	if (i < 2) return i;
	else return fib(i-1) + fib(i-2)
}
```

This quickly gets out of hands: 
	the two recursive calls on the `return` statement either take ages to terminate (try 40!) or results in a `StackOverflowError`.
There are just too many calls of `fib` for larger `i`.

Here's a version of `fib` that has only a single unnested recursive call:

```java
long fib(long a, long b, int i) {
	if (i == 0) return a;
	else if (i == 1) return b;
	else return fib(b, a+b, i-1);
}
```

This not only terminates quickly even for large `i`, but some languages and VMs can optimize for this special situation.
Since the single recursive call is always at the end (just with altered parameters), all stack variables for this function can be reused, effectively turning _tail recursive_ functions into `for` loops.

> While Scala supports this, there is no official support for this in the JVM yet.


# Functions as First-Class Citizens

Enough of the torture with recursion, let's talk about more relevant aspects of functional programming in Java, and how functions as (kind of) first-class citizens can dramatically improve the readability of your code.


### for-each

A functional way to traverse our `List<T>` and print every element would be:

```java
static <T> void print(List<T> xs) {
	if (xs.isEmpty()) return;
	else {
		System.out.println(xs.head);  // (1)
		print(xs.tail);
	}
}
```

Obviously, printing is just one thing we might want to do with those elements.
Maybe we want to write them to a file, or send them via network?
But instead of duplicating the code and effectively only changing line (1) of the above example, we can _separate the concerns_ by adding a `Consumer<T>` ([link](https://docs.oracle.com/javase/9/docs/api/java/util/function/Consumer.html)) argument to our traversal function to be executed on each element.

```java
@FunctionalInterface
interface Consumer<T> {
	void accept(T t);
}
```
```java
static <A> void forEach(List<A> xs, Consumer<A> c) {
	if (xs.isEmpty()) return;
	else {
		c.accept(xs.head);
		forEach(xs.tail, c);
	}
}
```

And here's a `Consumer` that prints elements to `System.out`:

```java
List<Integer> xs = list(1, 2, 3, 4);
forEach(xs, new Consumer<Integer>() {
	@Override
	public void accept(Integer i) {
		System.out.println(i);
	}
});

// or shorter with lambda
forEach(xs, i -> System.out.println(i));

// or even shorter with method references
forEach(xs, System.out::println);
```


### filter

A different yet very frequent use of lists is to filter them by a particular predicate.
The result of `filter` is a list that contains only elements that satisfy some condition.
Let's do this right away with a helper "function", a `Predicate` ([link](https://docs.oracle.com/javase/9/docs/api/java/util/function/Predicate.html))

```java
@FunctionalInterface
interface Predicate<T> {
	boolean test(T t);
}
```
```java
static <A> List<A> filter(List<A> xs, Predicate<A> p) {
	if (xs.isEmpty()) return xs;
	else if (p.test(xs.head)) return list(xs.head, filter(xs.tail, p_));
	else return filter(xs.tail, p);
}
```

```java
List<Integer> xs = list(1, 2, 3, 4);
List<Integer> lt3 = filter(xs, i -> i < 3);
```


### map

The last functional concept for this class is `map`.
When working with data, you often need to transform one type of data into another.
For example, you might retrieve a list of `Student`, but you actually need only a list of their family names.
That is: given a list of type `Student`, you want a list of type `String`.

```java
static List<String> familyNames(List<Student> xs) {
	if (xs.isEmpty()) return empty();
	else return list(xs.head.getFamilyName(), familyNames(xs.tail));
}
```

Well, this seems fairly generic, doesn't it?
You want to _map_ one object to another, given some [_function_](https://docs.oracle.com/javase/9/docs/api/java/util/function/Function.html).
Let's try this again, with the logic moved to a functional interface:

```java
@FunctionalInterface
interface Function<A, B> {
	B apply(A a);
}
```
```java
static <A, B> List<B> map(List<A> xs, Function<A, B> f) {
	if (xs.isEmpty()) return empty();
	else return list(f.apply(xs.head), map(xs.tail, f));
}
```

```java
List<Student> xs = ...;
List<String> fns = map(xs, s -> s.getFamilyName());
List<String> fns = map(xs, Student::getFamilyName);  // even shorter
```


Our functional approach to traversing, filtering and mapping data successfully separated the "iteration" from the actual logic, which was provided as a "function as an object".


# FP in Java: Streams

So far, we did all the exercises with a pretty useless list class.
But `filter`, `map` and `forEach` are essential tools to process data.

In Java, these functional aspects are not attached (and thus limited) to lists, but to a more general concept of (possibly infinite) [_data streams_](https://docs.oracle.com/javase/9/docs/api/java/util/stream/Stream.html).
This is more appropriate, since the data may originate from very different sources: web APIs, database result sets, or plain text files.

We'll talk more about `Stream`s next week, but for now, please take note of the following methods:

- `Stream<T>.filter(Predicate<? super T> p)`
- `Stream<T>.map(Function<? super T, ? extends R>)`
- `Stream<T>.forEach(Consumer<T> consumer)`

In our examples above, the `filter` and `map` methods returned new lists.
Here, these _intermediate_ methods return `Stream`s.
Our `forEach` method had return type `void`; here, this _terminal_ operation also returns `void`.

Recall the (iterative) example from the very top: retrieve a list of students, find those who attended a certain class, and then print out the names of the classes on their transcript of records.

```java
for (Student s : Database.getStudents()) {
	if (s.getClasses().contains("Programmieren 3")) {
		Transcript tr = Database.getToR(s.getMatrikel());
		for (Record r : tr)
			System.out.println(r);
	}
}
```

In functional Java, this becomes (in a most detailed variant)

```java
Database.getStudents().stream()
	.filter(s -> s.getClasses().contains("Programmieren 3"))
	.map(Student::getMatrikel)
	.map(Database::getToR)
	.flatMap(t -> t.records.stream())  // stream of lists to single list
	.forEach(System.out::println);
```

Isn't that much more precise as the nested `for` loops with `if` and method calls?


### Lazy Evaluation

One last word on efficiency.
The stream methods are _lazy_ in a sense that the downstream operations are only applied to the actual results of the previous steps.
To stick with the example above, the `Student::getMatrikel` would only be applied to those who were passed on by `filter`.
In other words: the _terminal_ operation (here: `forEach`) _pulls_ data from the streams, all the way to the originating stream.


<p style="text-align: right">&#8718;</p>

