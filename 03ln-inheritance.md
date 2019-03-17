---
title: Inheritance
permalink: /03ln-inheritance/
---

# Inheritance

In the [previous class](/02ln-classes-interfaces/), we looked at classes and interfaces (foremost) in the context of information hiding and encapsulation: within a package, use public interfaces and package-visible classes to implement the functionality.

In this class, we look at both again but focus on inheritance.

# Extending Classes vs. Implementing Interfaces

Although similar from a technical point of view, extending classes and implementing interfaces are two very different concepts.

Consider the following example, which makes use of both.

```java
class Shape {
	private int x, y;
	Shape(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
```
```java
interface Drawable {
	void draw(Canvas c);
}
```
```java
class Rectangle extends Shape implements Drawable {
	private int width, height;
	Rectangle(int x, int y, int w, int h) {
		super(x, y);
		width = w;
		height = h;
	}
	public void draw(Canvas c) { /* do some magic */ }
}
```

The `Rectangle` literally `extends` a general `Shape`: aside from `x` and `y` coordinates, it is defined by `width` and `height`.

The `Rectangle` also `implements` -- adheres to the contract of -- `Drawable`: given some `Canvas`, it can draw itself.

Following the semantics of the keywords, you should
- _extend_ a class, when you aim to make something more specific; a `Rectangle` will always be a `Shape`.
- _implement_ an interface, when you aim to extend a class by certain (potentionally orthogonal) functionality; not every `Shape` might be drawable, and there might be other classes which happen to be drawable.



# Abstract Classes

Sometimes, it makes sense to enforce that subclasses implement certain methods.
For example, every shape will cover a certain surface; however, different shapes will have different ways to compute that.


```java
abstract class Shape {
	// ...
	public abstract double surface();  // no method body!
}
```
```java
class Rectangle extends Shape {
	// ...
	public double surface() {
		return 0;
	}
}
```

Note that 
- a class with at least one `abstract` method must be declared `abstract`, too.
- a subclass of an `abstract` class must either implement all abstract methods, or be declared `abstract` as well.
- abstract classes that implement interfaces are not required to provide implementations for the interface methods.

Why would you use abstract classes to begin with?
Consider the following example that models the entities of a database; you already discovered that you need to create SQL INSERT statements to be used down the road.

```java
interface DBItem {
	String makeInsertSQL();
}
```
```java
class Student implements DBItem {
	private String name;
	private int matrikel;
	public String makeInsertSQL() {
		return "INSERT INTO student (name, matrikel) VALUES (" 
			+ name + ", " + matrikel + ")";
	}
}
```
```java
class FWPM implements DBItem {
	String name, description;
	int numPart;
	public String makeInsertSQL() {
		return "INSERT INTO fwpm (name, numPart, description) VALUES (" 
			+ name + ", " + numPart + ", " + description + ")";
	}
}
```

> Note: In a real project with databases, you should use an [ORM](https://en.wikipedia.org/wiki/Object-relational_mapping) to automate the interactions/query generation.

> Note: In a real project, you [must sanitize](https://commons.apache.org/proper/commons-lang/javadocs/api-2.6/org/apache/commons/lang/StringEscapeUtils.html) your data before putting it in the SQL statement

As you can see, the `makeInsertSQL` implementations are fairly similar, and duplicated code often leads to errors.

Ideally, the mechanics of generating the SQL would be done once, and the actual model classes would only provide the relevant details.
This is where abstract classes come in:

```java
abstract class DBItem {  // note: could also use interface and default methods
	String makeInsertSQL() {
		return "INSERT INTO " + getTable() + " (" + getFields()) 
			+ ") VALUES (" + getValues() + ")";
	}

	abstract String getTable();
	abstract String getFields();
	abstract String getValues();
}
```
```java
class Student extends DBItem {
	private String name;
	private int matrikel;
	String getTable() { 
		return "student"; 
	}
	String getFields() {
		return "name, matrikel";
	}
	String getValues() {
		return name + ", " + matrikel;
	}
}
```

This way, we the SQL statement is constructed solely in the `DBItem`, where it also makes sense -- the INSERT statement only differs in table, fields and values.
The subclasses on the other hand provide the necessary information, but are agnostic of how to construct the queries.


# Final Classes and Methods

Sometimes, you want to prevent/prohibit that a method is overwritten, or a class/interface is extended.
In the example above, you may want to secure the `DBItem.makeInsertSQL` method, so that nobody accidentally introduces errors in a subclass.
Similarly, you might want to prevent subclasses of `FWPM`.
To do so, use the keyword `final`:

```java
abstract class DBItem {
	final String makeInsertSQL() {
		// ...
	}

	// ...
}
```
```java
final class FWPM extends DBItem {
	// ...
}
```

Note that if a class is `final`, all methods are implicitly `final`.

Why does the following code produce a warning?

```java
class SomeClass {
	final public static void method() {  // why does this produce a warning?
		// ...
	}
}
```


# Inheritance and Shadowing

Similar to nested (inner) classes, name conflicts lead to shadowing.
Consider the following example:

```java
interface Intf {
	default void method() {
		System.out.println("Intf.method()");
	}
}
```
```java
class Base implements Intf {
	public void method() {
		Intf.super.method();  // access default method
		System.out.println("Base.method()");
	}
}
```

From basic inheritance, you already know that you can access the super*class*'s implementation of a method by using `super.<methodname>()`.
Similarly, you can use `<Interface>.super.<methodname>()` to access the default methods provided by the implemented interface.
Note however, that this only works from *within* the class; from the outside, dynamic binding follows these rules: 
- Instance methods are preferred over interface default methods.
- Methods that are already overridden by other candidates are ignored.


# Multiple Inheritance

As you know, Java is _single-inheritance_ only, i.e. a class `extends` exactly one superclass (if none specified: `Object`).
However, sometimes you might want to inherit from two different classes.

Consider the following example:

```java
class Van {
	List passengers;
	void board(Person p) {
		passengers.add(p);
	}
	void unboard(Person p) {
		passengers.remove(p);
	}
}
```
```java
class Pickup {
	List cargos;
	void load(Cargo c) {
		cargos.add(c);
	}
	void unload(Cargo c) {
		cargos.remove(c);
	}
}
```

What if your new class is both, a van *and* a pickup? <a href="https://commons.wikimedia.org/wiki/File%3AVolkswagen_Transporter_Pick-up_(13936076527).jpg">Academic example, you say</a>?

```java
class VwTransporterPickup extends Van, Pickup { // compiler error :-(
} 
```

One solution is to define `Van` and `Pickup` as `interface`:

```java
interface Van {
	void board(Person p);
	void unboard(Person p);
}
```
```java
interface Pickup {
	void load(Cargo c);
	void unload(Cargo c);
}
```
```java
class VwTransporterPickup implements Van, Pickup {
	List passengers, cargos;
	void board(Person p) {
		passengers.add(p);
	}
	// ...
}
```

But this requires us to implement all the methods explicitly, which is against the philosophy of storing code where it semantically belongs to.
The solution is similar to the approach we followed for abstract classes: use `default` methods in the `interfaces`, along with abstract methods that give access to the attributes.

```java
interface Van {
	List getPersons();
	default void board(Person p) {
		getPersons().add(p);
	}
	// ...
}
```
```java
class VwTransporterPickup implements Van, ... {
	private List persons;
	public List getPersons() {
		return persons;
	}
	// ...
}
```

As you can see, interfaces with default methods allow for a very modular and flexible architecture.


# The Diamond Problem

<https://en.wikipedia.org/wiki/Multiple_inheritance#The_diamond_problem>

Consider the following diagram and its implementation:

![Diamond Problem](/assets/diamond-problem.svg)


```java
interface Top {
	void method();
}
```
```java
interface Left extends Top {
	default void method() {
		System.out.println("Right.method()");
	}
}
```
```java
interface Right extends Top {
	default void method() {
		System.out.println("Right.method()");
	}
}
```
```java
class Bottom implements Left, Right {
	public void method() {
		System.out.println("Right.method()");
		// use <Interface>.super.<methodname> to access default methods
		Left.super.method();
		Right.super.method();
	}

	public static void main(String... args) {
		Bottom b = new Bottom();
		b.method();
	}
}
```

The diamond problem describes a name conflict that arises from a class hierarchy, where two implemented classes have the same name.
In our example, the interfaces `Left` and `Right` add default (different) implementations for `method()`.
Use `super.<method>` to access the implementation of a **base class** (here: none given), but use `<Interface>.super.<method>` to access default methods.

Note that commenting out `Bottom.method()` (i.e. not overwriting `method()`) will lead to a compiler error because of the ambiguity among the default implementations.


# The Decorator Pattern

The previous case with (sort-of) multiple inheritance is a rather rare situation.
A similar yet different (and more frequent) situation is where you have similar objects (or classes) that should exhibit different behavior while maintaining the same interface.

Consider the following example: You're implementing the networking stack of your application, and you can transmit payload over your connection/socket.
A payload is a rather abstract concept, but you know that ultimately it comes down to some text:

```java
abstract class Payload {
	abstract String getText();
}
```
For now, you're implementing a text based protocol, so you're essentially sending plain ASCII text:

```java
class TextPayload extends Payload {
	private String text;
	TextPayload(String text) {
		this.text = text;
	}

	@Override
	String getText() {
		return text;
	}
}
```

Now you've (hopefully :-) learned two things in your networking class:
1. If you're sending larger amounts of data, you should use compression.
2. If you're sending sensitive data (such as logins), you should use encryption.
3. If you're sending large amounts of sensitive data (such as media), you should use both.

However, you want to stick to the `Payload` signature, and separate out the configuration (text? compression? encryption?) from the actual logic.

```java
Payload textPayload = new TextPayload(data);  // :-)
Payload payload = guessPayload(data);  // is it encrypted? compressed? both?
String content = payload.getText();
```

One way to make this modular and flexible is to use the _decorator pattern_ as depicted in the diagram:

![decorator pattern](/assets/decorator-pattern.svg)

The key is that the `PayloadDecorator` maintains a reference to a "source" Payload (the instance it's decorating) and does not yet implement the abstract `getText()` method.
Now consider the implementing classes:

```java
class GzipPayload extends PayloadDecorator {
	GzipPayload(Payload deflated) {
		super(deflated);
	}
	String getText() {
		String balloon = getSource().getText();
		return "inflate(" + balloon + ")";
	}
}
```
```java
class EncryptedPayload extends PayloadDecorator {
	EncryptedPayload(Payload encrypted) {
		super(encrypted);
	}
	String getText() {
		String cipher = getSource().getText();
		return "decrypt(" + cipher + ")";
	}
}
```

The following example illustrates, how the decorator can be used:

```java
Payload text = new TextPayload("some deflated and encrypted text");
Payload inflated = new GzipPayload(text);
Payload decrypted = new EncryptedPayload(inflated);

System.out.println(text.getText());
// "some deflated and encrypted text"
System.out.println(inflated.getText());
// inflate("some deflated and encrypted text")
System.out.println(decrypted.getText());
// decrypt(inflate("some deflated and encrypted text"))
```

As you can see, the decorator patern allows to configure arbitrary chaining of regular, gzip and encrypted payloads.
This pattern is also used in the JDK, and you might have already come across it:

```java
FileInputStream fis = new FileInputStream("/objects.gz");
BufferedInputStream bis = new BufferedInputStream(fis);
GzipInputStream gis = new GzipInputStream(bis);
ObjectInputStream ois = new ObjectInputStream(gis);
SomeObject someObject = (SomeObject) ois.readObject();
```


<p style="text-align: right">&#8718;</p>
