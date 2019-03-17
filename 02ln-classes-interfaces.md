---
title: Classes and Interfaces
permalink: /02ln-classes-interfaces/
---

# Classes and Interfaces


## Information Hiding

> Note: Most of this section should be familiar to you already.

_Information hiding_ (or _encapsulation_) is a fundamental concept in object-oriented programming.
The idea is to group information and algorithms to coherent modules, and have those modules reference each other.
In Java, this is realized using _interfaces_ (keyword `interface`) to describe the externals of modules, and _classes_ (keyword `class`) that encapsulate information (variables) and business logic (methods).
Storing an instance of a class that _implements_ (keyword `implements`) an interface in a reference to said interface illustrates the principle of _information hiding_.

```java
interface Intfc {
	void method1();
}
```
```java
class Klass implements Intfc {
	void method1() {
		System.out.println("Hello, World!");
	}
	void method2() {
		System.out.println("Uh, might be hidden");
	}
}
```
```java
Klass inst1 = new Klass();
Intfc inst2 = new Klass();  // ok-- since Klass implements Intfc

inst1.method1();
inst2.method1();  // ok-- method guaranteed

inst1.method2();  // ok-- reference of type Klass
inst2.method2();  // error: methdo2() not provided by Intfc
```

As you can see, regardless of the actual implementation, you can "see" only what's on the class or interface definition.


## Restricting visibility

Enter packages and visibility modifiers.
Typically, you will group your classes and interfaces into coherent _modules_, the packages.
Packages are organized in a hierarchical way, similar to a filesystem: while the identifier uses `.` as a separator, each level "down" will be in the according directory.
For example, the package `de.fhro.inf.prg3` would correspond to the directory `de/fhro/inf/prg3`, and Java files inside that directory need to have the preamble `package de.fhro.inf.prg3` to alert the compiler of the package this class belongs to.

Now recall the [visibility modifiers that are defined in Java](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html):
- `public`: visible everywhere (apply to class, attributes or methods)
- `private`: visible only within the class (apply to attributes or methods)
- `protected`: visible within the class, package **and** in derived classes (apply to attributes or methods; more next week)
- _(no modifier)_: visible within the _package_, but not visible outside of the package (apply to class, attributes or methods)

Both of these features combined yield excellent information hiding:

```java
package de.fhro.inf.prg3;
public interface Itfc {
	void method();
}
```
```java
package de.fhro.inf.prg3;
class SecretImpl implements Itfc {
	public void method() {  // note: interface --> public
		System.out.println("Hello, World!");
	}
	void secret() {
		System.out.println("Only accessible within this package!");
	}
}
```
```java
package de.fhro.wif.prg3;     // note: different package...
import de.fhro.inf.prg3.Itfc; // ...thus must import!

Itfc itfc = ...;  // we'll come to this later!
itfc.method();

// de.fhro.inf.prg.SecretImpl not visible
// only methods of .Itfc are accessible
```


## Interfaces revisited

Prior to Java 8, interfaces were limited to (public) functions.
Since Java 8, interfaces can provide `default` implementations for methods (used to maintain backwards compatibility on extended interfaces) which are available on every resource, and can implement `static` methods, which can be used without instances.
Since Java 9, interfaces can have `private` methods.

Reconsider the above code example:

```java
package de.fhro.inf.prg3;
public interface Itfc {
	void method();
	static Itfc makeInstance() {
		return new SecretImpl();
	}
	default void method2() {
		System.out.println("Ah, seems not implemented!");
	}
}
```
```java
package de.fhro.wif.prg3;
import de.fhro.inf.prg3.Itfc;

Itfc itfc = Itfc.makeInstance();
itfc.method();   // provided by (hidden) SecretImpl
itfc.method2();  // provided by default implementation
```

Use `static` on interface methods just like you would on class methods.
Use `default` to provide a default implementation, which can be overridden by the implementing class.

> Note: Depending on your JVM security settings, you can use reflection to get around information hiding and to inspect the actual class of the instance; we'll cover that later in this class.

Sometimes, it is useful to use helper functions.
Since Java 9, you can use the `private` keyword to implement regular and static helper functions; this makes interfaces very close to pure abstract classes.

```java
interface Itfc {
	default void a() {
		System.out.println("Hello, I'm (a)");
		c();
	}
	default void b() {
		System.out.println("Hello, I'm (b)");
		c();
	}
	private void c() {
		System.out.println("Yay, (c) only implemented once!");
	}

	// same for static
	static void d() {
		System.out.println("Hello, I'm (d)");
		f();
	}
	static void e() {
		System.out.println("Hello, I'm (e)");
		f();
	}
	private static void f() {
		System.out.println("Yay, (f) only implemented once!");
	}
}
```

### Name Conflicts

Since in Java classes can implement multiple interfaces, you may end up with a name conflict:

```java
interface Itfc1 {
	default void greet() { System.out.println("Servus"); }
}
interface Itfc2 {
	default void greet() { System.out.println("Moin"); }
}

// must implement `greet()` to resolve name conflict
class Example implements Itfc1, Itfc2 {
	public void greet() {
		Itfc2.super.greet();  // use super to specify which implementation
	}
}
```


## Classes and `static`

Recall the `static` modifier, used inside class definitions.
In the following example, all instances of class `Klass` share the very same `n`; this variable "lives" with the class definition.
Each instance will have its own `p`, since it is *not* static.

```java
class Klass {
	private static n = 0;
	static int nextInt() {
		return n++;
	}
	private int m = 0;
	void update() {
		m = nextInt();
	}
}
```

Calling `nextInt()` anywhere will return the current value and then increment by one.
The `update()` method can only be called on instances, but will use the very same `nextInt`.

```java
int n1 = Klass.nextInt();  // n1 == 0, Klass.n == 1
Klass k = new Klass();
k.update();                // k.m == 1, Klass.n == 2
int n2 = k.nextInt();      // n2 == 2, Klass.n == 3
```

Note that static attributes and methods can be called from both the class and the instance.
To avoid misunderstandings, use the class when accessing static members.

Typical use cases for static members are constants, shared counters, or the Singleton pattern.

### Static Initializers

As you can see in the example above, static attributes are typically immediately initialized (particularly if they're `final`).
If the value is not just a simple expression, you can use a _static initializer block_ `static { /* ... */ }` to do the work:

```java
class Klass {
	static final int val;
	
	static {
		// do what you like...
		val = Math.sqrt(3.0);
	}
}
```


## Nested Classes

Sometimes, a class logically belongs _inside_ another class, since it might not make sense on its own.

### Inner Classes

Consider the following example of a simple binary tree: every node has a left and a right child; the tree is defined by its root node.
The class that represents the node very specific to the `BinaryTree` (and presumably not useful to other classes), thus we make it an inner class:

```java
class BinaryTree {
	private class Node {
		Object item;
		Node left, right;
	}
	Node root;
}
```

Inner classes can have accessibility modifiers (`private`, `protected`, `public`), and are defined within the enclosing class's `{}` (the order of attributes is irrelevant).

> Note: Inner classes are also compiled, and stored as `Outer$Inner.class`.

All attributes and methods of the outer class are available to the inner class -- regardless of their accessibility level!
This is also the reason that instances of (regular) inner classes can only exist with an instance of the corresponding outer class.
Potential shadowing of variables by inner class can be resolved by using the class name:

```java
class Outer {
	int a;
	class Inner {
		int a;
		void m() {
			System.out.println(a);  // Outer.Inner.a
			System.out.println(Outer.this.a);
		}
	}
}
```

Like other members, inner classes can also be `static`; in this case, the inner class can be used without an instance of the enclosing class:

```java
class Outer {
	static class StaticInner {

	}
	class Inner {

	}
}
```
```java
Outer.StaticInner osi = new Outer.StaticInner();  // ok
```

To instantiate the inner class outside of the outer class, instantiate the outer class first:

```java
Outer.Inner oi = new Outer.Inner();  // error: must have enclosing instance
Outer.Inner oi = new Outer().new Inner();
```

> Note: In most cases, inner classes are instantiated inside the enclosing class, and are typically private


### Anonymous Classes

Far more often, you will be using anonymous innter classes.
Recall the sorting function `java.util.Collections.sort(List<T> list, Comparator<? super T> c)` (ignore the `<...>` for now).
You might have used this as follows:

```java
class MyComparator implements Comparator {
	public int compareTo(Object o1, Object o2) {
		// ...
	}
}
```
```java
Collections.sort(mylist, new MyComparator());
```

While this works just fine, you have one more extra class, just to carry the actual comparison code.
Anonymous classes help keeping your class hierarchy clutter-free:

```java
Collections.sort(mylist, new Comparator() {
	public int compareTo(Object o1, Object o2) {
		// ...
	}
});
```

Note that it says `new Comparator() {}`: While it is true that you cannot instantiate interfaces, this syntax is shorthand for creating a new class that implements the `Comparator` interface.
This is also works for an anonymous derived class (`new Klass() {}`).

The syntax is compelling, but comes with one major drawback: [anonymous classes cannot have a constructor](http://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.9.5.1).
Instead, Java replicates the current scope, that is: all _effectively_ final variables are available within the class.

```java
final Object ref;
Collections.sort(mylist, new Comparator() {
	{
		System.out.println(ref);  // anonymous initializer block
	}
	public int compareTo(Object o1, Object p2) {
		if (o1.equals(ref))
			// ...
	}
})
```

Similar to the static initializer block (`static {}`), you can use an anonymous initializer block (`{}`).


### Local Classes

The last variant is the _local class_, which is essentially the same as an anonymous inner class, but can be defined with a constructor.

```java
class Klass {
	void example() {
		class Local {
			int m;
			Local(int m) {
				this.m = m;
			}
		}

		Local l1 = new Local(3);
	}
}
```

Note that the enclosing class can again be referenced as `Klass.this. ...`.


### Interfaces

The same concepts holds for Interfaces, however thoser are implicitly `static`.


## Functional Interfaces, Lambda Expressions and Method References

A functional Interface is an interface that has exactly one non-default method and is annotated with `@FunctionalInterface` (since Java 8).
For example:

```java
@FunctionalInterface
interface Filter {
	boolean test(Object o);
}
```
```java
class Klass {
	void filter(Filter f) {
		// ...
	}
}
```
```java
Klass k1 = new Klass();
k1.filter(new Filter() {
	public boolean test(Object o) {
		return o != null;
	}
});
```

As you can see, there is a lot of "boilerplate" code beside the actual `test()` function.
You can write this more compact with a lambda expression:

```java
k1.filter(o -> o != null);  // single statement
k1.filter(o -> {  // multiple statements, conclude with return
	return o != null;
})
```

In this casel, the lambda expression `x -> ...` refers to a functional Interface, with the non-default function having exactly one argument.
If you have multiple arguments, the lambda expression becomes for example `(a1, h2) -> ...` (note that the type is inferred automatically).

The third alternative is to use a method _reference_:

```java
@FunctionalInterface
interface Filter {
	boolean test(Object o);
	static boolean testForNull(Object o) {
		return o != null;
	}
}
```
```java
Filter fi = new Filter() {
	public boolean  test(Object o) {
		return o != null;
	}
}
```
```java
k1.filter(fi);
k1.filter(fi::test);
k1.filter(Filter::testForNull);
```

Method references (`::`) can be specified in the following ways:

| Kind	| Example
|-------|--------
|Reference to a static method	| `ContainingClass::staticMethodName`
|Reference to an instance method of a particular object	| `containingObject::instanceMethodName`
| Reference to an instance method of an arbitrary object of a particular type	| `ContainingType::methodName`
| Reference to a constructor	| `ClassName::new`

and their usage can be confusing.
Consider this example:

```java
@FunctionalInterface
interface BiFunction {
	Object apply(Object a, Object b);
}
```
```java
class SomeObject implements BiFunction {
	public Object apply(Object o) {
		System.out.println(o);
		return null;
	}
	public static void main(String[] args) {
		SomeObject so = new SomeObject();
		BiFunction bf = so::apply;
	}
}
```

We'll review method references in the last two chapters of this class, when we're discussing Java's functional programming capabilities.

## Further Reading

- [When to Use Nested Classe etc.](https://docs.oracle.com/javase/tutorial/java/javaOO/whentouse.html)
- [method references](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html)

<p style="text-align: right">&#8718;</p>
