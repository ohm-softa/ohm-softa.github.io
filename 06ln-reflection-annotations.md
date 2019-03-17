---
title: Reflection and Annotations
permalink: /06ln-reflection-annotations/
---

# Reflection

_Type introspection_ is the ability of programming languages to determine the type (or its properties) of an arbitrary object at runtime.
Java takes this concept one step further with _reflection_, allowing not only to determine the type at runtime, but also modifying it.

At its core, reflection builds on `java.lang.Class`, a generic class that _is_ the definition of classes (which we already came across in the [previous chapter](/04ln-generics-1/)).
Note that most of the classes we will be using when dealing with reflection are in the package `java.lang.reflection` ([package summary](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/package-summary.html)), and (almost) none of them have public constructors -- the JVM will take care of the instantiation.

There are different ways to get to the class object of a Class:

```java
// at compile time
Class<String> klass1 = String.class;

// or at runtime
Class<? extends String> klass2 = "Hello, World!".getClass();
Class<?> klass3 = Class.forName("java.lang.String");
Class<String> klass4 = (Class<String>) new String().getClass();

System.out.println(klass1.toString());  // java.lang.String
System.out.println(klass2.toString());  // ditto...
System.out.println(klass3.toString());
System.out.println(klass4.toString());

klass1.getName();  // java.lang.String
klass1.getSimpleName();  // String
```

Note that for `Class<T>` you can use the class name if known at compile time (or use an unchecked cast at runtime), or use the `?` (wildcard) and appropriate bounds for _any_ type.

So what can you do with a [`Class<?>` object](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)?


## Basic Type Information

```java
// all of these will return true
int.class.isPrimitive();
String[].class.isArray();
Comparable.class.isInterface();
(new Object() {})
	.getClass()
	.isAnonymousClass();          // also: local and member classes
Deprecated.class.isAnnotation();  // we will talk about those later
```

As you can see, even primitive type like `int` or `float` have class objects (which are, by the way, different from their wrapper types `Integer.class` etc.!).


## Type Internals

```java
// family affairs...
String.class.getSuperClass();  // Object.class

// constructors
String.class.getConstructors();  // returns Constructor<String>[]

// public methods
String.class.getMethod("charAt", int.class);
String.class.getMethods();  // returns Method[]

// public fields (attributes)
String.class.getField("CASE_INSENSITIVE_ORDER");  // Comparator<String>
String.class.getFields();  // returns Field[]

// public annotations (more on this later)
String.class.getAnnotation(Deprecated.class);  // null...
String.class.getAnnotationsByType(Deprecated.class);  // []
String.class.getAnnotations();  // returns Annotation[]
```

As you would expect, these methods may throw `NoSuch{Method,Field}Exception`s and, more importantly, `SecurityException`s (more on security later).

Furthermore, you can distinguish between _declared_ fields (methods, ...), and "just" fields: `.getFields()` (and `.getMethods()` etc.) will return _the public_ fields of an object, including those inherited by base classes.
Use `.getDeclaredFields()` (and `.getDeclaredMethods()` etc.) to retrieve _all_ fields declared in this particular class.


## Object Instantiation
As you can see, you can also query for _constructors_ of a class.
This is the base for creating new instances based on class definitions:

```java
Class<?> klass = "".getClass();
String magic = (String) klass.newInstance();  // unchecked cast...
```

Sticking with the example of strings, the [documentation tells us](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html) that there exist non-default constructors.
Consider for example the `String(byte[] bytes)` constructor:

```java
Constructor<String> cons = (Constructor<String>) String.class
	.getConstructor(byte[].class);
String crazy = cons.newInstance(new byte[] {1, 2, 3, 4});
```

Note that this may trigger quite a few exceptions: `InstantiationException`, `IllegalAccessException`, `IllegalArgumentException`, `InvocationTargetException`, all of which make sense if you extrapolate their meaning from the name.


## Messing with Objects

### Modifying Fields (Attributes)
Remember [Rumpelstiltskin](https://en.wikipedia.org/wiki/Rumpelstiltskin)?
In short: A very ambitious father claimed his daughter could produce gold.
Put under pressure by the king, she cuts a deal with an imp: it spins the gold and in return she would sacrifice her first child -- _unless she could guess the imp's name!_

Ok, here's the mean imp and the poor daughter:

```java
class Imp {
	private String name = "Rumpelstiltskin";
	boolean guess(String guess) {
		return guess.equals(name);
	}
}
```
```java
class Daughter {
	public static void main(String... args) {
		Imp imp = new Imp();

		// to save your child...
		imp.guess(???);
	}
}
```

Since `Imp.name` is private, the imp feels safe (it's dancing around the fire pit...).
But can we help the daugher save her firstborn?
[Yes, we can!](https://en.wikipedia.org/wiki/Barack_Obama_presidential_campaign,_2008#Slogan)
Using reflection, we will sneak through the imp's "head" to find the string variable that encodes the name.

```java
Imp imp = new Imp();
String oracle = null;
for (Field f : imp.getClass().getDeclaredFields()) {  // get all fields
	f.setAccessible(true);  // oops, you said `private`? :-)
	if (Modifier.isPrivate(f.getModifiers())  // looking for `private String`
			&& f.getType() == String.class) {
		oracle = (String) f.get(imp);  // heureka!
	}
}
imp.guess(oracle);  // true :-)
```

Or alternatively, we could brainwash the imp.

```java
Imp imp = new Imp();
for (Field f : imp.getClass().getDeclaredFields()) {
	f.setAccessible(true);
	if (Modifier.isPrivate(f.getModifiers()) 
		&& f.getType() == String.class) {
		f.set(imp, "Pinocchio");  // oops :-)
	}
}
imp.guess("Pinocchio");  // true :-)
```

The `Field` class allows us to retrieve and modify both the modifiers and the values of fields (given an instance).

### Calling Functions

Similar to accessing and modifying fields, you can enumerate and invoke methods.
Sticking with the imp above, what if the imp's name were well-known, but nobody knew how to ask for a guess?

```
class WeirdImp {
	static final String name = "Rumpelstiltskin";
	private boolean saewlkhasdfwds(String slaskdjh) {
		return name.equals(slaskdjh);
	}
}
```

This time, the `name` is well known, but the guessing function is hidden.
Again, reflection to the rescue.

```java
WeirdImp weirdo = new WeirdImp();
for (Method m : weirdo.getClass().getDeclaredMethods()) {
	m.setAccessible(true);
	if (m.getReturnType() == boolean.class  // ...returns boolean?
			&& m.getParameterCount() == 1   // ...has one arg?
			&& m.getParameterTypes()[0] == String.class) {  // which is String?
		System.out.println(m.invoke(weirdo, Weirdo.name));
	}
}
```


## Basic Java Beans

Reflection can be used to facilitate an architecture where code is dynamically loaded at runtime.
This is often called a _plugin mechanism_, and [Java Beans](https://docs.oracle.com/javase/tutorial/javabeans/) have been around for quite a long time.

Consider this simple example: We want to have a game loader that can load arbitrary text-based games which are provided as a third party `.jar` file.

```java
package reflection;
public interface TextBasedGame {
	void run(InputStream in, PrintStream out) throws IOException;
}
```

A simple _parrot_ (echoing) game could be:

```java
package reflection.games;
public class Parrot implements TextBasedGame {
	@Override
	public void run(InputStream in, PrintStream out) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		out.println("Welcome to parrot. Please say something");

		String line;
		while ((line = reader.readLine()) != null && line.length() > 0) {
			out.println("You said: " + line);
		}
		out.println("Bye!");
	}
}
```

These games all implement the `TextBasedGame` interface, and their `.class` files can be [packaged into a jar](https://docs.oracle.com/javase/tutorial/deployment/jar/build.html).
Later, if you know the location of the jar file, you can load classes by-name:

```java
package reflection;
public class TextGameLoader {
	public static void main(String... args) throws 
		IOException, ClassNotFoundException, IllegalAccessException, 
		InstantiationException {

		// load classes from jar file
		URL url = new URL("jar:file:/Users/riko493/git/hsro-inf-prg3/example/games.jar!/");
		URLClassLoader cl = URLClassLoader.newInstance(new URL[] {url});

		// you can play "Addition" or "Parrot"
		final String choice = "Parrot";

		TextBasedGame g = (TextBasedGame) cl.loadClass("reflection.games." + choice)
				.newInstance();
		g.run(System.in, System.out);
	}
}
```

Check out this [games.jar](/assets/games.jar), which provides the games `reflection.games.Addition` and `reflection.games.Parrot`.


## Security

The previous sections showed clearly how powerful the tools of reflection are.
Naturally, security is a concern: what if someone loads your jars, enumerates all classes, and then tries to steal passwords from a user?

This has indeed been done, and is the reason why Java was historically considered insecure or even unsafe to use.
However, newer versions of Java have a sophisticated [system of permissions and security settings](https://docs.oracle.com/javase/8/docs/api/java/lang/SecurityManager.html) that can limit or prevent reflection (and other critical functionality).

Two things that do _not_ work, at least out of the box:
- While you can do a forced write on `final` _fields_, this typically does not affect the code at runtime, since the values are already bound at compiletime.
- It is impossible to swap out _methods_ since class definitions are read-only and read-once.
	If you wanted to facilitate that, you would have to write your own class loader.


## Object Comparison

One last word regarding reflection and object comparison.
As you know, we distinguish two types of equality: reference and content-based equality.

```java
class K {
	K(String s) {
		this.s = s;
	}
}

int a = 1;
int b = 1;

a == b;  // true: for primitive types, the values are compared

K k1 = new K("Hans");
K k2 = new K("Hans");

k1 == k2;       // false: they point to different memory
k1.equals(k2);  // ???
```

If you don't overwrite the `.equals` method in your class, the default version `Object.equals` will check for reference equality.
If you overwrite it, you need to make sure to
- test for `null`
- test for reference equality (same memory?)
- test for same _type_
- call `super.equals()`, if it was overwritten there
- compare all attributes

Consider this implementation of `equals`:

```java
public boolean equals(Object o) {
	if (o == null) return false;
	if (o == this) return true;   // same memory

	// version A
	if (!this.getClass().equals(o.getClass()))
		return false;
	// version B
	if (this.getClass() != o.getClass())
		return false;
	// version C
	if (!(o instanceof K))
		return false;

	if (!super.equals(o))
		return false;

	// now compare attributes
	return this.s.equals(((K) o).s);

}
```

Here's the question: Which of the versions A, B or C are correct ways to test if the _types_ of the two objects match?

A) is correct, since we compare both runtime classes with `.equals`.

B) is correct, since the class objects are shared among all instances (and parametrized generics; recall type erasure).

C) is however _incorrect_: the `instanceof` operator would also return `true` if there is a match on an interface of derived class.


# Annotations

Equipped with the tools to introspect classes both at compile and run time, we can now dive into annotations.

Annotations are _meta-information_, they can be attached to classes, methods or fields, and can be read by the compiler or using reflection at runtime.
They are denoted using the `@` character, for example `@Override`.


## Defining Annotations

Annotations are similar to interfaces: both as in syntax and as in a method or field can have multiple annotations.

```java
public @interface Fixed {
    String author() ;
    String date() ;
    String bugsFixed() default "" ;
}
```

This defines the annotation `@Fixed(...)` with three arguments; the last one is optional and defaults to the empty string.

```java
@Fixed(author="riko493", date="2017-11-15")
void method() { ... }
```

In general, there are _marker anotations_ (e.g. `@Deprecated`) without arguments, _value annotations_ (e.g. `@SuppressWarnings("...")`) that take exactly one value, and more sophisticated annotations (e.g. `@Fixed(...)` above).

```java
public @interface SomeValue {
	String value();
}
```
```java
@SomeValue("meh") void method() { ... }
```

### Meta-Annotations
Even annotations can be annotated.
_Meta annotations_ define where and how annotations can be used.

- `@Target({ElementType.FIELD, ElementType.METHOD})`: Use this to limit your custom annotation to fields or methods.
- `@Retention(RetentionPolicy.{RUNTIME,CLASS,SOURCE)}`: This controls if the annotation is available at runtime, in the class file, or only in the source code.
- `@Inherited`: Use this to make an annotation to be passed on to deriving classes.


## Method Annotations

Here are a few popular _method_ annotations, some of those you may have come across already:

```java
class K {
	@Override
	public boolean equals(Object o) {
		// ...
	}
	@Deprecated
	public void useSomethingElseNow() {
		// ...
	}
	@SuppressWarnings("unchecked")
	public void nastyCasts() {

	}
}
```

What are they good for?
- `@Override` is used to signal the intent of overwriting; results in compile error if its actually no overwrite (e.g. `@Override public boolean equals(K k)`)
- `@Deprecated` marks a method not to be used anymore; it might be removed in the future.
- `@SuppressWarnings(...)` turns off certain compiler warnings

## Type Annotations

Here are a few _type_ annotations ([source](https://blogs.oracle.com/java-platform-group/java-8s-new-type-annotations)):
- `@NonNull`: The compiler can determine cases where a code path might receive a null value, without ever having to debug a `NullPointerException`.
- `@ReadOnly`: The compiler will flag any attempt to change the object.
- `@Regex`: Provides compile-time verification that a `String` intended to be used as a regular expression is a properly formatted regular expression.
- `@Tainted` and `@Untainted`: Identity types of data that should not be used together, such as remote user input being used in system commands, or sensitive information in log streams.


# Examples of Annotations

## JUnit5
The new [JUnit5](http://junit.org/junit5) test drivers inspect test classes for certain annotations.

```java
class MyTest {
	BufferedReader reader;

	@BeforeAll
	void setUp() {
		reader = new BufferedReader();  // ...
	}

	@Test
	void testSomeClass() {
		// ...
	}
}
```

Most of the time, you will get around with `@BeforeAll`, `@AfterAll` and `@Test`; see this [complete list of annotations](http://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations).


## Gson by Google

[Gson](https://github.com/google/gson) by Google helps with de/serializing objects (see today's assignment).

It allows you to map between JSON and Java objects:

```java
class Klass {
	private int value1 = 1;
	private String value2 = "abc";
	@SerializedName("odd-name") private String oddName = "1337";
	private transient int value3 = 3;  // will be excluded
	Klass() {
		// default constructor (required)
	}
}

// Serialization
Klass obj = new Klass();
Gson gson = new Gson();
String json = gson.toJson(obj);  
// ==> json is {"value1":1,"value2":"abc","odd-name": "1337"}

// Deserialization
Klass obj2 = gson.fromJson(json, Klass.class);
// ==> obj2 is just like obj
```

## Others

- [Hibernate](http://hibernate.org/): sophisticated object storage
- [Butterknife](http://jakewharton.github.io/butterknife/): GUI bindings for Android ridiculously simplified
- [Retrofit](https://github.com/square/retrofit): consume REST interfaces without any pain


> Historic remark: The APT was removed in Java8, see <http://openjdk.java.net/jeps/117>

<p style="text-align: right">&#8718;</p>
