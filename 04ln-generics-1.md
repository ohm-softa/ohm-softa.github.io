---
title: "Mixins, Pt.1; Generics, Pt. 1"
permalink: /04ln-generics-1/
---

# Mixins, part one.

In the past two weeks, we reviewed [classes and interfaces](/02ln-classes-interfaces/) as well as [inheritance and abstract classes](/03ln-inheritance/).
The Decorator pattern was a good example, how abstract classes (or interfaces) can be used to add functionality to existing classes _while maintaining the original class contract_.
A related pattern is the _Mixin_, which allows to add functionality to a class without inheritance (thus, no shared class contract).

Consider the following example:
You would like to implement the data model for an instant messenger, and start with a basic class `Message` to store the text.

```java
class Message {
	private String t;

	public Message(String t) { this.t = t; }

	public String text() {
		return t;
	}
}
```

Soon after, you realize that people sometimes need to shout at each other, which on the internet happens as [all-caps](https://www.netlingo.com/word/shouting.php).

```java
class EscalatedMessage extends Message {
	public EscalatedMessage(String t) { super(t); }
	public String escalated() { 
		return text().toUpperCase();  // HEY I'M SHOUTING AT YOU!
	}
}
```

Also, it seems people love sending emoticons 😄, which turn out to be nothing more than [unicode charaters](https://en.wikipedia.org/wiki/Emoticons_(Unicode_block)), which need to be coded into byte arrays to send them over the internet.

```java
class UnicodeMessage extends Message {
	public UnicodeMessage(String t) { super(t); }
	public byte[] utf8() {
		return text().getBytes(Charset.forName("UTF-8"));
	}
}
```

So far so good, here's our small world:

![Messages](../assets/mixin-diamond-1.svg)

But what if you want to SHOUT WITH EMOTICONS 😈😈???
If Java were to support multiple inheritance, we could do the following

```java
class EscalatedUnicodeMessage extends EscalatedMessage, UnicodeMessage {
	// nope, not in Java...
}
```

Looking closer at both subclasses, you can see that they are foremost about extending the _behavior_ (methods) and not the _model_ (attributes).
So what we actually want is to augment `Message` by further functionality-- which we can do using `default` methods in interfaces.
The data required for the functionality is then injected using non-default methods:

```java
interface Escalatable {
	// this is where we will get the data needed for our functionality
	String text();

	// this is the actual code, implemented only once!
	default String escalate() {
		// yes, we can refer to non-default methods here
		return text().toUpperCase();
	}
}
```
```java
interface Unicodable {
	String text();
	default byte[] utf8() {
		return text().getBytes(Charset.forName("UTF-8"));
	}
}
```

Attaching those interfaces to the `Message` class _mixes in_ the functionality:

```java
class Message implements Escalatable, Unicodable {
	private String t;
	public Message(String t) { this.t = t; }

	// required by both interfaces
	public String text() { 
		return t; 
	}

	// that's it, all done!
}
```

```java
class App {
	public static void main(String... args) {
		Message m = new Message("Hans");

		System.out.println(m.text());      // "Hans"
		System.out.println(m.escalate());  // "HANS"
		byte[] data = m.utf8();
	}
}
```

This way of adding functionality (not attributes!) to a class is called a _mixin_.
Related topics are _aspect oriented programming_, which focuses on functionality rather than objects/relations, and _dependency inversion_, where both high- and low-level modules depend on abstractions.



# Generics, part one.

> Note: Many of the following concepts and rules also apply to other languages that support generics, such as Scala or C++.

The example data structure for this class will be a _map_.
Unlike a _list_ which stores data in a (fixed) sequential order, a _map_ is an associative container that stores a certain _value_ for a certain (unique) _key_.
Compare the basic interfaces:

```java
interface List {
	void add(Object o);  // appends to the list
	Object get(int i);   // retrieves the i-th element
}
```
```java
interface Map {
	void put(Object key, Object value);  // stores object for key
	Object get(Object key);              // retrieves object for key
}
```

There are many ways to implement a map, and they differ greatly in complexity.
Here, let's consider a very basic implementation that consists of map entries (key and value) that are stored as a list.
Remember, [_inner classes_](/02ln-classes-interfaces/) are an excellent means to keep the class hierarchy neat and organized:

```java
class SimpleMapImpl implements Map {
	private class Entry {
		Entry(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
		Object key;
		Object value;
		Entry next;
	}
	// ...
}
```

Similar to a linked list, use a `head` element and the `Entry.next` reference to build up the list.
When `put`-ting a value, iterate from `head` to the end, and either update the `value` where the `key` matches, or append a new `Entry` at the end.

```java
class SimpleMapImpl implements Map {
	// ...
	private Entry head;

	@Override
	public void put(Object key, Object value) {
		if (head == null) {
			head = new Entry(key, value);  // easy: first Entry in
			return;
		}

		Entry it = head, prev = null;
		while (it!= null) {
			if (it.key.equals(key)) {  // key exists, update value
				it.value = value;
				return;
			}
			prev = it;
			it = it.next;
		}

		prev.next = new Entry(key, value);  // append at the end
	}
	// ...
}
```

Simiarly, when `get`-ting an element, iterate from the `head` to the end, and return that `value` where the `key` matches, or `null`.

```java
class SimpleMapImpl implements Map {
	// ...
	@Override
	public Object get(Object key) {
		Entry it = head;
		while (it != null) {
			if (it.key.equals(key))  // found it!
				return it.value;
			it = it.next;
		}

		return null;  // no value for this key
	}
}
```

Clearly, this implementation is among the worst choices when it comes to performance; can you specify the complexity for `put` and `get` in Landau (\\(O\\))notation?

> Food for thought: How would you improve this implementation to achieve better performance?

Here's how you would use it:

```java
class App {
	public static void main(String... args) {
		Map map = new SimpleMapImpl();

		// the type conversion to Object is automatic
		map.put("Grummel Griesgram", 143212);
		map.put("Regina Regenbogen", 412341);

		// since the return type is Object, explicit type conversion is required
		Integer grummel = (Integer) map.get("Grummel Griesgram");    // > 143212
		Integer schleichmichl = (Integer) map.get("Schleichmichl");  // > null
	}
}
```

Since the signatures of `Map` prescribe `Object` as return type, an explicit type conversion is required.
Well, this is a nuisance, but you could call [inheritance](/03ln-inheritance/) to the rescue to provide the appropriate behavior:

```java
class SimpleStringIntMapImpl extends SimpleMapImpl {
	public Integer get(String key) {
		Object val = super.get(key);
		if (val == null)
			return null;
		else
			return (Integer) val;
	}
}
```
```java
class App {
	public static void main(String[] args) {
		SimpleStringIntMapImpl map = new SimpleStringIntMapImpl();
		map.put("Hans", 14235);
		Integer hans = map.get("Hans");  // e voila!
	}
}
```

While this implementation works, it has some drawbacks which may lead to serious runtime errors.
Consider this example, which perfectly compiles:

```java
class App {
	public static void main(String[] args) {
		Map map = new SimpleStringIntMapImpl();  // nota bene: Map!
		map.put("Schleichmichl", "DROP TABLE matrikel");
		Integer schleichmichl = map.get("Schleichmichl");
	}
}
```

If you run it, you will get a _runtime_ error:

```
Exception in thread "main" java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
	at SimpleStringIntMap.get(SimpleStringIntMapImpl.java:14)
	at App.main(App.java:24)
```

> Note: Even worse, if you were to use a `key` different from `String`, the `get` call would bind to the super class' `get(Object)` method.
> Use the `@Override` annotation to have the compiler warn you of unintentional overloading instead of overwriting.


# Generic Classes (Interfaces) to the Rescue

> Note: The following works equally for classes and interfaces.

On top of the runtime issues, one would have to `extend` for each key-value type combination to be used, i.e. the `Map` should be literally generic.
Clearly, this is an all but ideal situation which can be fixed using Java _generics_ (introduced in Java 1.5).

Here's the idea: Instead of using `Object` along with type casts, use type parameters (type variables) in lieu of actual types, i.e. instead of `Object`, use `T`.
The type parameters need to be declared in the signature of the class, in `<...>` and between the name and the opening curly parenthesis:

```java
interface Map<K, V> {
	void put(K key, V value);
	V get(K key);
}
```

While you could use any identifier for the type parameters, it is customary to use single letters.
Use `T` for a single type, `K` and `V` for key and value, `R` and `S` for unrelated types (see towards the end of this class).
If you need to use multiple type parameters, separate them with comma.

When implementing or extending a generic interface or class, you may either set actual types for the parameters (1), carry over the parameter list (2), or a mix of the two (3).

```java
// (1) define actual types: all type parameters bound
class SimpleStringIntMapImpl implements Map<String, Integer> {
	public void put(String key, Integer value) {
		// ...
	}
	// ...
}
```
```java
// (2) carry over type list: still two type parameters
class SimpleMapImpl<K, V> implements Map<K, V> {
	public void put(K key, V value) {
		// ...
	}
	// ...
}
```
```java
// (3) partially carry over; here: one type parameter remains
class SimpleStringMapImpl<V> implements Map<String, V> {
	public void put(String key, V value) {
		// ...
	}
	// ...
}
```

Coming back to the example with the basic map, we can now make use of generics:

```java
public class SimpleMapImpl<K, V> implements Map<K, V> {
	class Entry {
		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		K key;
		V value;
		Entry next;
	}

	Entry head;

	@Override
	public void put(K key, V value) {
		// ...
	}

	@Override
	public V get(K key) {
		// ...
	}
}
```
```java
class App {
	public static void main(String[] args) {
		Map<String, Integer> map = new SimpleMapImpl<>();  // note: type inferred!
		map.put("Hans", 14235);
		Integer i = map.get("Hans");  // > 14235
		
		map.put("Peter", "Willi");  // compile time error!
	}
}
```

As you can see, `SimpleMapImpl` now features _type safety_: instead of failing at runtime with a `ClassCastException`, it now fails at compile time with a type error.

## Generics and Static

Note that the inner class was declared non-static:

```java
public class SimpleMapImpl<K, V> implements Map<K, V> {
	class Entry {
		public Entry(K key, V value) { /* ... */ }
		Entry next;
		// ...
	}
	// ...
}
```

Accordingly, we can use anything of the enclosing instance, including the type arguments (since it can only exist with the outer instance).
If you were to use a static inner class, you need to make it generic as well:

```java
public class SimpleMapImpl<K, V> implements Map<K, V> {
	static class Entry<K, V> {
		public Entry(K key, V value) { /* ... */ }
		Entry<K, V> next;
		// ...
	}
	// ...
}
```

> Note: Here, the static inner class uses the same names `<K, V>`; this is arbitrary, they could also be named `<Y, Z>`.
> Since the class is static, `K` and `V` of the outer class are not visible.


# Type Erasure and Raw Type

Internally, the Java compiler actually removes the generic parameters after compilation.
If you look at the compiled classes, you will find

```
.
├── Map.class
├── SimpleMapImpl$Entry.class
└── SimpleMapImpl.class
```

The type validation is completely done at compile time by replacing the type parameters with `Object` and the respective type casts, thus automating a previously error prone process.
On completion, there is no need to retain the generic parameters, and only the "basic" .class file is stored.
This has three side effects.
- You can't distinguish between types based their type parameters.
- There is no (direct) way to instantiate an _array_ of generic elements.
- You can instantiate the so-called _raw type_ of a generic class.

```java
class Example<T> {
	private T inst = new T();           // compiler error!
	private T[] wontWork = new T [10];  // compiler error!

	public static void main(String... args) {
		Example e0 = new Example();  // raw type; effectively T := Object
		Example<String> e1 = new Example<>();   // T := String
		Example<Integer> e2 = new Example<>();  // T := Integer
		
		System.out.println(e0.getClass() == e1.getClass());  // > true!
		System.out.println(e1.getClass() == e2.getClass());  // > true!
	}
}
```

If you must instantiate from a generic type, you need to pass in the type parameter information ("the class") at runtime, and the type must have a default constructor.
This is done using Java's reflection mechanism (which will be covered in detail in two weeks).
The runtime type information is stored in the `.class` attribute of a class or interface; it is of type `Class<T>` where the `T` is bound to the type itself.
We can exploit that to generate instances of generic types at runtime:

```java
class Example<T> {
	private T inst;     // definition ok-- no `new` yet!
	private T[] array;

	Example(Class<T> clazz) 
			throws IllegalAccessException, InstantiationException {
		inst = clazz.newInstance();  // uses default constructor
		array = (T[]) Array.newInstance(clazz, 5);  // size of 5
	}

	public static void main(String... args) {
		Example<String> e = new Example<>(String.class);  // pass in actual type!
	}
}

```


# Generic Methods

Similar to classes and interfaces, type parameters can be attached to methods to make them generic.

Consider this example, where we want to reverse the order of elements in an array.

```java
class Example {
	static Object[] reverse(Object[] arr) {
		Object[] clone = arr.clone();
		for (int i = 0; i < arr.length/2; i++) {
			swap(clone, i, arr.length - 1 - i);
		}
		return clone;
	}
	private static void swap(Object[] arr, int i, int j) {
		Object h = arr[i];
		arr[i] = arr[j];
		arr[j] = h;
	}
	public static void main(String... args) {
		Integer[] arr = {1, 2, 3, 4, 5};
		Integer[] rev = (Integer[]) reverse(arr);  // explicit type cast

		// will produce `ClassCastExecption` at runtime!
		Integer[] oha = (Integer[]) reverse(new String[] {"Hans", "Dampf"});
	}
}
```

For methods, the type parameters are specified prior to the return type, and type parameters can be used both for arguments and return types.

```java
class Example {
	static <T> T[] reverse(T[] in) {
		T[] clone = in.clone();
		for (int i = 0; i < in.length/2; i++) {
			swap(clone, i, in.length - 1 - i);
		}
		return clone;
	}
	private static <T> void swap(T[] arr, int i, int j) {
		T h = arr[i];
		arr[i] = arr[j];
		arr[j] = h;
	}
	public static void main(String... args) {
		Integer[] arr = {1, 2, 3, 4, 5};
		Integer[] rev = reverse(arr);  // type safety at compile time!

		// will produce error at compile time! (Integer[] and String[] incompatible)
		Integer[] oha = (Integer[]) reverse(new String[] {"Hans", "Dampf"});
	}
}
```

Generic methods will be particularly useful with the introduction of [bounds (next week)](/05ln-generics-2/).


<p style="text-align: right">&#8718;</p>
