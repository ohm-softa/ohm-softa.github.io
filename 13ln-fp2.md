---
title: "Functional Programming in Java"
permalink: /13ln-fp2/
---

# Reduction

Let's start with a recap of the three main concepts of [last weeks lecture](/12s-fp1/).
Working on lists (and later streams), we defined three main methods:

- `filter` utilizing a `Predicate<T>` to retain only certain elements,
- `map` utilizing a `Function<T, R>` to transform a list of elements of type `T` to a list of type `R`, and finally
- `forEach` utilizing a `Consumer<T>` that accepts (in order of the list).

Working on lists, we defined those _recursively_:

```java
static <T> List<T> filter(List<T> xs, Predicate<T> p) {
	if (xs.isEmpty()) return xs;
	else if (p.test(xs.head)) return list(xs.head, filter(xs.tail, p));
	else return filter(xs.tail, p);
}
```

```java
static <A, B> List<B> map(List<A> xs, Function<A, B> f) {
	if (xs.isEmpty()) return empty();
	else return list(f.apply(xs.head), map(xs.tail, f));
}
```

```java
static <A> void forEach(List<A> xs, Consumer<A> c) {
	if (xs.isEmpty()) return;
	else {
		c.accept(xs.head);
		forEach(xs.tail, c);
		// return (added for clarity)
	}
}
```

Here are three key observations:

1. All three methods "iterate" over the list, i.e. all elements are visited.
2. The `forEach` method is _tail recursive_, as in the recursive call is the very last one prior to `return`.
3. The `filter` and `map` methods return another list, while `forEach` returns nothing (`void`).

In this (final) chapter, we'll talk about list (or stream) _reduction_, that is reducing a sequence of values to a single value.

# Reduce

Let's start with a simple example: sum all numbers of a list.

```java
static int sum(List<Integer> xs) {
	if (xs.isEmpty()) return 0;  // sum of an empty list is zero
	else return xs.head + sum(xs.tail);
}
```

For `list(1, 3, 3, 7)`, this function evaluates to

```
sum(list(1, 3, 3, 7))
-> 1 + sum(list(3, 3, 7))
-> 1 + (3 + sum(list(3, 7)))
-> 1 + (3 + (3 + sum(list(7))))
-> 1 + (3 + (3 + (7 + sum(empty()))))
-> 1 + (3 + (3 + (7 + 0)))
-> 1 + (3 + (3 + 7))
-> 1 + (3 + 10)
-> 1 + 13
-> 14
```

As you can see, the recursion expands until the terminal case is reached, and the first `return` happens.
Then the addition is done all the way back up the call stack.

From last week, we know that this is unfavorable: the recursion depth is as many as there are list elements.
Here is a better, tail recursive variant:

```java
static int sum(List<Integer> xs, int z) {
	if (xs.isEmpty()) return z;
	else return sum(xs.tail, z + xs.head);
}
```

which evaluates to

```
sum(list(1, 3, 3, 7), 0)
-> sum(list(3, 3, 7), 0 + 1)
-> sum(list(3, 7), 1 + 3)
-> sum(list(7), 4 + 3)
-> sum(empty(), 7 + 7)
-> 14
```

As mentioned last week, tail [recursive calls are much more efficient](/12ln-fp1/#detour-tail-recursion); depending on the language, they can be realized as a for-loop reusing the stack variables.

Let's consider another example: joining Strings together by concatenating them.

```java
static String join(List<String> xs, String z) {
	if (xs.isEmpty()) return z;
	else return join(xs.tail, z + xs.head));
}
```

Clearly, the `sum` and `join` functions are almost identical -- the only difference being the `Integer` and `String` types.
So why not generalize?

```java
static <T> T reduce(List<T> xs, T z) {
	if (xs.isEmpty()) return z;
	else return reduce(xs.tail, z + xs.head);  // oops :-(
}
```

Unfortunately, the `+` operator is only defined for basic types (including `java.lang.String`), and Java does not support operator overloading.

But look closer what the `+` actually is: it is a binary operation to combine two values to a single value of the same type.
Both `String` and `Integer` actually offer such methods:

```java
static int reduce(List<Integer> xs, int z) {
	if (xs.isEmpty()) return z;
	else return sum(xs.tail, Integer.sum(z, xs.head));
}
static String reduce(List<String> xs, String z) {
	if (xs.isEmpty()) return z;
	else return join(xs.tail, z.concat(xs.head));
}
```

Let's isolate the operation, using the interface `java.util.function.BinaryOperator<T>`:

```java
interface BinaryOperator<T> {
	T apply(T t1, T t2);
}
```
```java
static <T> T reduce(List<T>, T z, BinaryOperator<T> op) {
	if (xs.isEmpty()) return z;
	else return reduce(xs.tail, op.apply(z, xs.head), op);
}
```
```java
reduce(list(1, 3, 3, 7), 0, (i, j) -> Integer.sum(i, j));  // 14
reduce(list(1, 3, 3, 7), 0, Integer::sum);
```
```java
reduce(list("a", "b", "c", "d"), "", (a, b) -> a.concat(b));  // abcd
reduce(list("a", "b", "c", "d"), "", String::concat);
```

It may sound odd, but `forEach` is actually a special case of `reduce`:

```java
reduce(list(1, 3, 3, 7), 0, (i, j) -> { System.out.println(j); return j; });
```


# Left Fold

The `reduce` function derived above is a bit restricted: it only works to reduce elements of type `T` to another `T`.
This might be a problem: consider the case where you sum up a very long list of potentially large `Integer`s -- you may run into an overflow.
The solution to this would be to add the `Integer`s from the list to a [BigInteger](https://docs.oracle.com/javase/9/docs/api/java/math/BigInteger.html) which is of arbitrary precision.
In terms of a `for`-loop, this would be

```java
BigInteger sum = BigInteger.ZERO;
for (Integer i : xs) {
	sum = sum.add(BigInteger.valueOf(i));
}
```

So our hypothetical `reduce` function for this would be

```java
static BigInteger reduce(List<Integer> xs, BigInteger z) {
	if (xs.isEmpty()) return z;
	else return reduce(xs.tail, z.add(BigInteger.valueOf(xs.head)));
}
```
```java
reduce(list(1, 3, 3, 7), BigInteger.ZERO);
```

By now, you probably already guessed it: we'll isolate the actual operation!
We need a function that takes a `BigInteger` (the accumulator), adds an `Integer`, and returns a `BigInteger`.

We'll do so with the interface `java.util.function.BiFunction<T, U, R>` (but tying `T` and `R`), and naming it `foldl` (read: _fold left_).

```java
static <T, R> R foldl(List<T> xs, R z, BiFunction<R, T, R> op) {
	if (xs.isEmpty()) return z;
	else return foldl(xs.tail, op.apply(z, xs.head), op);
}
```
```java
foldl(xs, BigInteger.ZERO, (b, i) -> b.add(BigInteger.valueOf(i)));
```

The function is called _left fold_, since the list is folded _to the left_, if you were to look at the evaluation:
```
foldl(list(1, 3, 3, 7), 0)
-> foldl(list(3, 3, 7), 0+1)
-> foldl(list(3, 7), 1+3)
-> foldl(list(7), 4+3)
-> foldl(empty(), 7+7)
-> 14
```
and visualized that a list, the operations are performed in this order:
```
// start at the bottom left!
        op
       / \
      op  7
     / \
    op  3
   / \
  op  3
 / \
z   1
```

Look at that list again, doesn't it look oddly familiar?
If we define `z` is the empty list, and `op` is the list constructor, you end up with the _reverse_ of the original list:

```java
foldl(list(1, 3, 3, 7), List.<Integer>empty(), 
	(xs, x) -> list(x, xs));  // 7, 3, 3, 1
```


# Right Fold

Let's go back to the original, _non-tail-recursive_ definition of `sum`:

```java
static int sum(List<Integer> xs) {
	if (xs.isEmpty()) return 0;  // sum of an empty list is zero
	else return xs.head + sum(xs.tail);
}
```
```java
static BigInteger sum(List<Integer> xs, BigInteger z) {
	if (xs.isEmpty()) return z;
	else return BigInteger.valueOf(xs.head).add(sum(xs.tail, z));
}
```

If you isolate the operation (`+` or `.add()`, respectively), you end up with a _right fold_:

```java
static <T, R> R foldr(List<T> xs, R z, BiFunction<T, R, R> op) {
	if (xs.isEmpty()) return z;
	else return op.apply(xs.head, foldr(xs.tail, z, op));
}
```
```java
foldr(list(1, 3, 3, 7), BigInteger.ZERO, 
	(i, b) -> BigInteger.valueOf(i).add(b));  // 14
```

Again, look at the order of operations:

```
 op
 / \
1  op
   / \
  3  op
     / \
    3  op
       / \
      7   z
```

To complete the top most operation, you need decend all the way down the fold.

Again, does that look familar?
If we define `z` as a list and `op` as the list construction, we end up with `append`:

```java
foldr(xs, List.<Integer>list(49), (z, zs) -> list(z, zs));
// 1, 3, 3, 7, 49
```

If we add in some logic, we get `map`:

```java
foldr(xs, List.<Integer>empty(), (z, zs) -> list(z*z, zs));
// squares: 1, 9, 9, 49
```

And even `filter`:

```java
// retain all values less than 5
foldr(xs, List.<Integer>empty(), (z, zs) -> {
	if (z < 5) return zs;
	else return list(z, zs);
});
// 7
```


## Tail Recursive Map

Unfortunately, right fold is _not_ tail-recursive, making it an undesirable operation.
The trick is to apply a left fold twice: in a first step, we'll use `foldl`  to _reverse_ the list, then we'll use it _again_ to reverse it to its original order and applying the mapping function:

```java
static <T, R> List<R> maptr(List<T> xs, Function<T, R> op) {
	List<T> reverse = foldl(xs, empty(), (ys, y) -> list(y, ys));
	List<R> mapped = foldl(reverse, empty(), (ys, y) -> list(op.apply(y), ys));
	return mapped;
}
```


# Advanced Stream Operations in Java

The [Java docs](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps) has a nice summary of stream operations.
The main distinction is between _intermediate_ operations which return a new stream, and _terminal_ operations, which return a (single) value.

## Intermediate

You already know most of the intermediate operations:
- `filter(Predicate<T> p)` removes/skips unwanted elements in the stream
- `map(Function<T, R> f)` transforms a `Stream<T>` into a `Stream<R>` using the provided `Function`
- `sorted(Comparator<T> comp)` returns a sorted stream
- `concat(Stream<T> s)` appends another stream
- `distinct()` removes duplicates
- `skip(int n)` and `limit(int n)` skip elements and truncate the stream

Another notable intermediate operation is `flatMap` which transforms a stream of sequences (lists, streams, etc.) into a single _flat_ sequence.

```java
// list-of-lists
Stream<List<Integer>> lol = Stream.of(
  Arrays.asList(1, 2), 
  Arrays.asList(3, 4), 
  Arrays.asList(5)
);

Stream<Integer> integerStream = lol.flatMap(al -> al.stream());
integerStream.forEach(System.out::print);  // 12345
```

## Terminal Operations

### Iteration and Reduction

Last week, we already talked about `forEach(Consumer<T> c)` which can be used to iterate over the whole stream, and pass each element to the `Consumer`.

This week, we learned about the `reduce` functions, which are implemented in Java as `reduce(T identity, BinaryOperator<T> op)` and the more more generic `reduce(U identity, BiFunction<U, ? super T, U> op, BinaryCombiner<U> com)`.

```java
Stream.of(1, 3, 3, 7).reduce(0, Integer::sum));
// 14

Stream.of(1, 3, 3, 7).reduce(BigInteger.ZERO, 
	(bi, i) -> bi.add(BigInteger.valueOf(i)), 
	(bi1, bi2) -> bi1.add(bi2)));  // combine identity with first result
// 14
```

> Nota Bene: The second operation can often be defined simpler as a `map` followed by a `reduce`

Another powerful tool provided by the Java Streams API is `collect` which is a special form of stream reduction.
The idea is to iterate over the stream and pass each element to a _combiner_ that builds up a data structure.
A classic example is to turn a `Stream` into a `List`:

```java
List<Integer> list1 = new LinkedList<>();
Stream.of(1, 3, 3, 7).forEach(i -> list.add(i));

// or shorter, using collect
List<Integer> list2 = Stream.of(1, 3, 3, 7).collect(Collectors.toList()));
```

Java provides a [lengthy list of collectors](https://docs.oracle.com/javase/9/docs/api/java/util/stream/Collectors.html) for your convenience.
Here are a few examples from the docs, most notably `groupingBy` und `partitioningBy`.

```java
// Accumulate names into a List
List<String> list = people.stream()
	.map(Person::getName)
	.collect(Collectors.toList());

// Accumulate names into a TreeSet
Set<String> set = people.stream()
	.map(Person::getName)
	.collect(Collectors.toCollection(TreeSet::new));

// Convert elements to strings and concatenate them, separated by commas
String joined = things.stream()
	.map(Object::toString)
	.collect(Collectors.joining(", "));

// Compute sum of salaries of employee
int total = employees.stream()
	.collect(Collectors.summingInt(Employee::getSalary));

// Group employees by department
Map<Department, List<Employee>> byDept = employees.stream()
	.collect(Collectors.groupingBy(Employee::getDepartment));

// Compute sum of salaries by department
Map<Department, Integer> totalByDept = employees.stream()
	.collect(Collectors.groupingBy(Employee::getDepartment,
		Collectors.summingInt(Employee::getSalary)));

// Partition students into passing and failing
Map<Boolean, List<Student>> passingFailing = students.stream()
	.collect(Collectors.partitioningBy(s -> s.getGrade() <= 400));
```


### Finding Values in a Stream

Often you need to find certain values in a stream, such as `findFirst()`, `min()` or `max()`.
Since these are methods that are often used on streams that are potentially empty, they return an [Optional](https://docs.oracle.com/javase/9/docs/api/java/util/Optional.html).

Optionals are similar to futures, as in you can `get()` the content if it `isPresent()`.
They can also be mapped to another `Optional`, or used as a `.stream()`.


### Verifying Values in a Stream

Another frequent use case it to verify if all, any or none of the elements in a stream match a certain criteria.
Use the `allMatch`, `anyMatch` and `noneMatch` functions, which take a `Predicate<T>` as argument.


## Parallel Processing

There is a separate document on [parallel streams](https://docs.oracle.com/javase/tutorial/collections/streams/parallelism.html), but in short, just use `parallelStream()` to enable parallel processing.
For example, to group People by their gender, you can use

```java
Map<Person.Gender, List<Person>> byGender = allPeople
	.stream()
	.collect(Collectors.groupingBy(Person::getGender));

// or parallel
ConcurrentMap<Person.Sex, List<Person>> byGender = allPeople
	.parallelStream()
	.collect(Collectors.groupingByConcurrent(Person::getGender));
```

<p style="text-align: right">&#8718;</p>
