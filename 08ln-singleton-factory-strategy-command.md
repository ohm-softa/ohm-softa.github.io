---
title: Singleton, Factory, Strategy and Command
permalink: /08ln-singleton-factory-strategy-command/
---

# Design Patterns, pt. 2

This week we'll talk about two structural patterns (_Singleton_ and _Factory_) as well as two behavioral patterns (_Strategy_ and _Command_).
We finish with _Dependency Injection_.
While this was not part of the original 23 patterns, it is used througout most advanced frameworks, and is closely linked to singleton and factory.


# Singleton

The task at hand is pretty simple: How can you ensure that a certain object is unique among your application?

In Java, there are a number of ways to realize that.
First, lets start with the obvious: we need to control who is allowed to create instances.
The safest thing to do is to make the constructor _private_.
If we can only create instances _from within the class_, we can allocate a static attribute at startup.

```java
class Singleton {
	public static final Singleton instance = new Singleton();

	private Singleton() {

	}
}
```
```java
Singleton.instance.doSomething();
```

This works if the constructor is trivial, and no further setup of `instance` is required.
But what if you need to do some extra work for `instance` to be ready-to-use?
The answer is: use a _static initializer block_.

And one more thing: the public visibility does not allow to guard the instance, e.g. from simultaneous access from multiple threads.
To fix this, use a getter method.

```java
class Singleton {
	private final static Singleton instance;

	static {
		instance = new Singleton();

		// do more work...
	}

	private Singleton() {

	}

	public static Singleton getInstance() {
		// guard if necessary...
		return instance;
	}
}
```
```java
Singleton.getInstance().doSomething();
```

The drawback of this solution is that the singleton is now instantiated at startup, and regardless if it is actually used.
To fix this, use a lazy initializer:

```java
class Singleton {
	private static Singleton instance;  // note: no final!

	public static Singleton getInstance() {
		if (instance == null) {
			instance = new Singleton();

			// do more stuff...
		}

		return instance;
	}

	private Singleton() {

	}
}
```
```java
Singleton.getInstance().doSomething();
```

All of these methods can be fooled using reflection.
If you truly want to disable further instantiation, use an `enum`, as described in [Bloch's Effective Java](https://www.amazon.de/Effective-Java-Edition-Joshua-Bloch/dp/0321356683).

```java
enum Singleton {
	INSTANCE;

	// specify more attributes, e.g. a counter
	int counter = 0;

	public int nextInt() {
		return counter++;
	}
}
```
```java
System.out.println(Singleton.INSTANCE.nextInt());  // "0"
System.out.println(Singleton.INSTANCE.nextInt());  // "1"
System.out.println(Singleton.INSTANCE.nextInt());  // "2"
```

Why woud you do this? ¯\\_(ツ)_/¯

No seriously.
It has to do with serialization: attributes of enums are not serialized (which can be a good or bad thing, it depends).
Most developers opt for the _lazy initialization_ method, since it makes the initialization more controllable.


## Structure and Participants

![dp-singleton](/assets/dp-singleton.svg)

- `Singleton`
	+ typically responsible for managing its unique instance
	+ provides operation to obtain unique instance (in Java: `static` method)


## Discussion

Singletons are standard practice to avoid resource conflicts or overallocation.
However, they are at the same time (strongly) discouraged if working in a multi-threaded (parallel) environment: while the actual resource conflict can be (usually) solved with locking, the process itself may dramatically reduce the benefit of parallel processing.

In fact, even the authors of _Design Patterns_ would recommend against using the singleton.
In a long interview on [SE-Radio](https://www.se-radio.net/2014/11/episode-215-gang-of-four-20-years-later/), Erich Gamma (at around minute 57) says (emphasis mine):

> So we added \[Singleton\] and this was an example, everybody understood, you’re writing C++, how you do Singleton-- have a static member and whatever. 
> Right. So everybody got that immediately.
> I think there was a great way to tell somebody what he talk about. 
> But if you think about it, doesn’t have a lot of coolness Singleton, right?
> **You add something global, which is kind of against objects, which are more about distributing things and not having this centralized thing, which will, at some point hurt you because you want to have multiple of these things or whatever, right.**
> **It’s often a design shortcut** to me, it’s this single as often as is, and shortcut, you have a well known place to get to another object rather than reaching to other objects to get this space. 
> So that’s why it often hurts, but with time.
> So that’s my, it’s not a, my top favorite, my favorite list of patterns.
> Right? 
> **So to me, it’s one of the things which should be voted off the Island: Singleton.**

In many cases, _Dependency Injection_ is the right pattern, if "wiring" of objects was the reason to use singletons in the first place.


## Examples

- Logging facilities
- Event busses and dispatch queues
- Device handles (there is only 1 physical device, e.g. `System.out`)
- Service objects (eg. API wrappers, ...)



---

# Strategy

# A Basic Example

The _strategy_ pattern helps to abstract a certain method from its actual implementation.
It is so fundamental to Java that it has the syntax keyword `interface` to separate _declarations_ from _implementations_.

Let's consider a simple example, where we want to sort a list in ascending or descending order, using different `Comparator<T>`s.

```java
List<Integer> list = new LinkedList<>();
list.add(4);
list.add(7);
list.add(1);
list.add(1);

Collections.sort(list, new Comparator<Integer>() {
	@Override
	public compare(Integer a, Integer b) {
		return a.compareTo(b);
	}
});

Collections.sort(list, new Comparator<Integer>() {
	@Override
	public compare(Integer a, Integer b) {
		return b.compareTo(a);  // note the flipped order!
	}
});
```

# A More Sophisticated Example

Check out [JavaKara](https://www.swisseduc.ch/informatik/karatojava/javakara/), a little robot bug that can be moved through a tiny world.

![kara-explore](/assets/kara-explore.png)

You can control it with very basic actions:

```java
public class Kara extends JavaKaraProgram {
	public static void main(String[] args) throws Exception {
		Kara k = new Kara();
		k.run("src/main/resources/world2.world");
	}

	@Override
	public void myMainProgram() {
		kara.move();        // one step forward
		kara.turnLeft();    // you guessed it...
		kara.turnRight();   // turn right
		kara.treeFront();   // tree ahead?
		kara.putLeaf();     // take a clover leaf
		kara.removeLeaf();  // remove a clover leaf		
	}
}
```

To have kara explore the whole room (starting from the center), you could think of two _strategies_:
- walk concentric growing circles until the room is fully explored
- walk to the top-right corner; then sweep left-to-right, top-to-bottom.

The sample code can be found in <https://github.com/ohm-softa/ohm-softa.github.io/tree/master/examples/src/main/java/designpattern/strategy>.
Check out the `StrategyExampleBad`, which has two explicit plans, `planA()` and `planB()`.
Contrast it with the implementation in `StrategyExample`: here, the logic of the strategy is moved to a separate class which is instantiated as needed.


## Structure

![dp-strategy](/assets/dp-strategy.svg)


## Discussion

The strategy pattern often shows up "in disguise."
For example, the `Stream.filter(Predicate<T> p)`, `Iterable.iterator()` or `Collection.sort(Comparator<T> c)` are all flavors of the strategy pattern: they allow to do the same thing in different ways.

You can easily spot potential refactoring areas if you have code such as

```java
if (isWav())
	return decodeWav(data);
else if (isMP3())
	return decodeMP3(data);
else
	return data.raw;
```

with the `decode{Wav,MP3}()` methods being members of the class.
Refactor to the strategy pattern by extracting them from the class and use them via a common interface.


## Examples

- `Comparator` interface, to customize sorting
- Encryption and authentication protocols
- Media encoders (mp3, mp4, aac, etc.)
- Serializers ("save as..." feature)


---

# Factory

A factory provides instances that fulfill a certain interface.

## A Basic Example

Let's consider a basic example: a package with public interfaces but package-private classes.

```java
package mypackage;

public interface Klass {
	void method();
}
```
```java
package mypackage;

class KlassImpl implements Klass {
	public void method() {
		System.out.println("Hello World!");
	}
}
```

So from outside the package, you can't instantiate `KlassImpl`:

```java
package someApp;
class MyApp {
	public static void main(String... args) {
		mypackage.Klass k = new mypackage.KlassImpl();  // not visible!
	}
}
```

This is where you need a factory method, often attached to an abstract class or as a static method to an interface.

```java
package mypackage;

public interface Klass {
	void method();
	static Klass create() {
		return new KlassImpl();  // inside package: visible!
	}
}
```
```java
mypackage.Klass k = mypackage.Klass.create();
```

As you can see, the _user_ of the package relies on the interface, and has no idea on which class was actually instantiated.


## A More Sophisticated Example: Factory and Strategy

Recall last week's [composite pattern](/07ln-iterator-composite-observer/) which can be found for example in the implementation of JSON or an XML tree.
In principal, both can store key-value relations, potentially nested:

```json
{
	"key": "value",
	"nested": {
		"key": "value"
	}
}
```
```xml
<element>
	<key>value</key>
	<element>
		<key>value</key>
	</element>
</element>
```

```java
interface Component {
	String toString();
}
interface Composite extends Component {
	void add(Component c);
}
interface Leaf extends Component {
}
```

With realizations for Json (`JsonComponent`, `JsonComposite`, `JsonLeaf`) and XML (`XmlComponent`, ...) that model similar structure, but different `toString()` serialization.

Without a factory, you would have to manually construct the composite:

```java
JsonComposite root = new JsonComposite("root");
root.add(new JsonLeaf("key", "value"));
Composite nested = new JsonComposite("nested");
nested.add(new JsonLeaf("key", "value"));
root.add(nested);
System.out.println(root);  // "root": {"key": "value", "nested": {"key": "value"}}
```

And similarly for `XmlComposite`.
If you abstract the instance creation into a factory, you could generalize the code significantly:

```java
interface CompositeFactory {
	Composite createComposite(String name);
	Leaf createLeaf(String name, String value);
}
```
```java
class JsonFactory implements CompositeFactory {
	@Override
	public Composite createComposite(String name) {
		return new JsonComposite(name);
	}

	@Override
	public Leaf createLeaf(String name, String value) {
		return new JsonLeaf(name, value);
	}
}
```

```java
CompositeFactory f = new JsonFactory();
// CompositeFactory f = new XmlFactory();

Composite root = f.createComposite("root");
root.add(f.createLeaf("key", "value"));

Composite nested = f.createComposite("nested");
nested.add(f.createLeaf("key", "value"));

root.add(nested);

System.out.println(root);
```

As you can see, you only need to replace the factory that produces the concrete classes; the construction logic remains the same.

You can find the above example code at <https://github.com/ohm-softa/ohm-softa.github.io/tree/master/examples/src/main/java/designpattern/factory>.


## Structure and Participants

![dp-factory](/assets/dp-abstract-factory.svg)

- `AbstractFactory`
	+ declares interface for operations that create the abstract products
- `ConcreteFactory`
	+ _implements_ the operations and procudes concrete products
- `AbstractProduct`
	+ declares interface for operations
- `ConcreteProduct`
	+ _implements_ the operations
- `Client`
	+ uses only interfaces declared by `AbstractFactory` and `AbstractProduct`


## Discussion

The factory pattern is omnipresent; sometimes it is realized as a single _factory method_, sometimes as a larger factory serving different objects.

The most common use is when developing against interfaces where the implementing classes are package-private.
The package would then expose a _factory_ that allows to generate instances that implement the public interfaces -- with internals hidden from the client.


## Examples

Typically objects that are either complicated to instantiate or which should not be exposed outside of a package.

- Iterators (probably the most frequently used factory)
- Objects that have complex instantiation protocols
- Logging instances
- API wrappers

---

# Command

Let's get back to kara for a moment.
We could write a program that takes input from the command line and uses that to direct kara around.

![kara-explore-2](/assets/kara-explore-2.png)

```java
public class InteractiveKara extends JavaKaraProgram {
	public static void main(String[] args) throws IOException {
		InteractiveKara program = new InteractiveKara();
		program.run("src/main/resources/world1.world");

		int c;
		while ((c = System.in.read()) != -1) {
			if (c == 10)
				continue;  // enter
			else if ((char) c == 'e')
				break;

			try {
				switch ((char) c) {
					case 'm': program.kara.move(); break;
					case 'l': program.kara.turnLeft(); break;
					case 'r': program.kara.turnRight(); break;
					case 't': program.kara.removeLeaf(); break;
					case 'd': program.kara.putLeaf(); break;
				}
			} catch (RuntimeException e) {
				System.out.println(e);
				System.exit(0);
			}
		}

		System.exit(0);
	}
}
```

Note the `catch` for `RuntimeException`: this happens if you have kara walk into a tree, or try to pick up a leaf where there is none.

So here is the problem: The above program works nicely until we hit a tree or otherwise raise an exception, at which point the while application is terminated.

Can you think of a mechanism that instead allows us to back-track where we came from?
Or in other words: if we screw up, can we undo the previous moves?

We can, if we take care of the following aspects:
- for every action, we need to know the reverse
- we need to keep track of every successful action
- (optionally) we can manually "forget" our history, if we're at a good place.

These are covered by the command pattern.
Instead of directly calling the actions on kara, we make _objects_ that will do the actual work:

```java
interface Command {
	void execute();
	default void undo() {
		throw new UnsupportedOperationException();
	}
}
```

Now, if we keep a journal (stack) of commands, it is easy to go back: just remove them one-by-one and call `.undo()`.

Here is an example for a command to move forward:

```java
class MoveCommand implements Command {
	private JavaKaraProgram.JavaKara kara;
	public MoveCommand(JavaKaraProgram.JavaKara kara) {
		this.kara = kara;
	}

	@Override
	public void execute() {
		kara.move();
	}

	@Override
	public void undo() {
		// turn back, move
		new TurnCommand(kara, 2).execute();
		kara.move();

		// turn to original direction
		new TurnCommand(kara, 2).execute();
	}
}
```

Let's alter the main program to use the command pattern:

```java
public class CommandExample extends JavaKaraProgram {
	public static void main(String[] args) throws IOException {
		CommandExample program = new CommandExample();
		program.run("src/main/resources/world1.world");

		// this will keep track of the successful commands
		Stack<Command> history = new Stack<>();

		int c;
		while ((c = System.in.read()) != -1) {
			// ...

			Command cmd = new IdleCommand();
			switch ((char) c) {
				case 'm': cmd = new MoveCommand(program.kara); break;
				case 'l': cmd = new TurnCommand(program.kara, -1); break;
				// ...
			}

			try {
				cmd.execute();
				history.push(cmd);
			} catch (RuntimeException e) {
				System.out.println(e);
				System.out.println("Tracking back to last saved state");
				// go back to beginning, restart
				while (history.size() > 0)
					history.pop().undo();
			}
		}

		System.exit(0);
	}
}
```

The complete example code can be found at <https://github.com/ohm-softa/ohm-softa.github.io/tree/master/examples/src/main/java/designpattern/command>.


## Structure and Participants

![dp-command](/assets/dp-command.svg)

- `Command`
	+ declares an interface for executing an operation
- `ConcreteCommand`
	+ _implements_ the operation
	+ uses the receiver as needed
- `Client` (application)
	+ creates `ConcreteCommand` and hands receiver
- `Invoker`
	+ actually calls `.execute()`
- `Receiver`
	+ the object used by the strategy


## Discussion

The command pattern is more frequent than you might initially think.
Think of it this way: whenever you allow the user to sequentially apply certain commands to your data/state, you may want to be able to undo those at some point.
Building up a stack of actions automatically leads to adopting the command pattern.


## Examples

- Editors that support undo or macros
- Databases with transaction/rollback support
- Filesystems with journaling
- Version control (eg. git)
- Realizations of automatons


# Dependency Injection

Object instantiation often comes with nested dependencies.
Consider the following (academic) example where one object requires another to work:

```java
class KlassA {}

class KlassB {
	KlassA a = new KlassA();
}
```

The problem with this implementation is that any `KlassB` instance is now hard-wired to a newly allocated `KlassA`.
While this may be fine for basic cases, it is often a deal breaker for systematic testing.
For example, if `KlassA` is a database or network service, testing `KlassB` would require a live connection to those services -- obviously not a smart solution: even if you could spin up a database solely for testing purposes, you would still have to maintain that.

So instead of hard-wiring dependencies, we apply _inversion of control_ (sometimes abbreviated as IOC), that is, instead of putting an object in control of instantiating its members, we provide ("inject") them through the constructor.
Let's rewrite and extend the example from above:

```java
class KlassA {}

class KlassB {
	KlassA a;
	KlassB(KlassA a) {
		this.a = a;
	}
	public String toString() { 
		return super.toString() + " a=" + a; 
	}
}

class KlassC {
	KlassA a;
	KlassB b;
	KlassC(KlassA a, KlassB b) {
		this.a = a;
		this.b = b;
	}
	public String toString() { 
		return super.toString() 
			+ " a=" + a 
			+ " b=" + b; 
	}
}
```

The instantiation of `KlassC` now requires the instantiation of its dependencies first:

```java
KlassA a = new KlassA();
KlassB b = new KlassB(a);
KlassC c = new KlassC(a, b);  // a is effectively singleton!
```

While this may be fine for small projects with simple classes, this can quickly get out of hand, particularly if testing requires mocking of certain dependencies.

The idea of _dependency injection_ (DI) as a pattern is to automate the process of object instantiation.
The [JSR330](https://jcp.org/en/jsr/detail?id=330) (2009) detailed how this should be done in Java.
Note that the idea is way older (rooting back to concepts in SmallTalk), but was probably first formalized by Martin Fowler in his [IOC and DI](https://martinfowler.com/articles/injection.html) article.
DI did not (yet) make it into the Java language specification, but it has been adopted and implemented by major frameworks, just to name a few: [Spring](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html) (setter-based injection related to Beans, see also [Baeldung on DI](https://www.baeldung.com/spring-dependency-injection)), [Dagger](https://dagger.dev) (compile-time DI for performance critical scenarios) or [Guice](https://github.com/google/guice) (reflection-based).

At its core, we need to define which fields or methods require injection, and what classes should be instantiated for certain interfaces.
Using [Guice](https://github.com/google/guice), we can rework the above example:

```java
@Singleton
class KlassA {}

class KlassB {
	KlassA a;
	
	@Inject
	KlassB(KlassA a) {
		this.a = a;
	}
	public String toString() { 
		return super.toString() + " a=" + a; 
	}
}

class KlassC {
	KlassA a;
	KlassB b;

	@Inject
	KlassC(KlassA a, KlassB b) {
		this.a = a;
		this.b = b;
	}
	public String toString() { 
		return super.toString() 
			+ " a=" + a 
			+ " b=" + b; 
	}
}
```

Guice can now provide an `Injector` that is effectively a factory; Guice uses modules (abstract base class `AbstractModule`) to manage object scopes.
It uses reflection to figure out how to instantiate the requested class or interface.

```java
Injector injector = Guice.createInjector(new AbstractModule(){});
KlassB b = injector.getInstance(KlassB.class);
KlassC c = injector.getInstance(KlassC.class);
```

Looking carefully, there is however one major difference to manual instantiation above: `b` and `c` will point to different instances of `KlassA`, since Guice will instantiate new objects where needed.
Thus, if we want to replicate the above behavior, we need to mark `KlassA` a singleton within the module:

```java
@Singleton class KlassA {}
```

Guice will then re-use the same instance where needed.
For completeness, Guice needs to be configured for interfaces:

```java
interface Itf {}
class KlassC implements Itf { /* ... */ }

Injector injector = Guice.createInjector(new AbstractModule() {
	@Override
	protected void configure() {
		bind(Itf.class).to(KlassC.class);
	}
});
```

An exhaustive introduction to Guice would go beyond the scope of this class.
However, the above should give you an idea how DI can be used to  reconfigure software for different environments (dev, staging, testing) by reconfiguring the module context to use mocking or restricted services as needed.

<p style="text-align: right">&#8718;</p>
