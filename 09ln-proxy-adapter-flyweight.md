---
title: Proxy, Adapter and Flyweight
permalink: /09ln-proxy-adapter-flyweight/
---

# Design Patterns, pt. 3

This week we'll focus on three structural patterns that are frequent in application development: _Proxy_, _Adapter_ and _Flyweight_.


# Proxy

Consider the following example.
You design a campus app that provides information such as timetables, room plans, cafeteria meal plan, etc.
The class responsible for retrieving the meal plan might look like this:

```java
class Meal {
	String name;
	List<String> notes;
}
```
```java
class MensaService {
	interface OpenMensaApi {
		@GET("canteens/229/days/{date}/meals")
		Call<List<Meal>> listMeals(@Path("date") String date);
	}

	OpenMensaApi api;

	MensaService() {
		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl("https://openmensa.org/api/v2/")
			.addConverterFactory(GsonConverterFactory.create())
			.build();

		api = retrofit.create(OpenMensaApi.class);
	}

	List<Meal> getMeals(String date) throws IOException {
		Call<List<Meal>> call = api.listMeals(date);
		Response<List<Meal>> resp = call.execute();
		return resp.body();
	}
}
```

Later in your app, you might use this class as follows:

```java
class SomeApp {
	public static void main(String... args) {
		MensaService ms = new MensaService();

		List<Meal> meals = ms.getMeals("20170612");
	}
}
```

You test your product and observe that students keep looking at the app every 5 minutes in the morning.
Clearly, every request to get the meals of a certain date will result in a subsequent (network) call to the OpenMensa API.
This is unfortunate: first, the remote server may become unreachable if wifi drops, or slow during "rush hour"; second (and more importantly), the information is quite static -- it usually doesn't change!

This is where the _proxy_ pattern comes in.
We create a subclass that satisfies the same interface as the base class, but adds caching functionality:

```java
class SomeApp {
	public static void main(String... args) {
		// anonymous derived class for brevity
		MensaService proxy = new MensaService() {
			Map<String, List<Meal>> cache = new HashMap<>();
			List<Meal> getMeals(String date) throws IOException {
				if (cache.containsKey(date))
					return cache.get(date);

				List<Meal> meals = super.getMeals(date);
				cache.put(date, meals);
				return meals;
			}
		};

		List<Meal> meals = proxy.getMeals("20170612");
	}
}
```

This way, the request for the meal plan of a certain date will only be executed once; for subsequent calls, the proxy returns the cached responses.
The fact that the proxy has the same interface allows the client to dynamically select to use the proxy or not.

> Note: Subclassing is one option; more frequently, both the "real" service and the proxy would implement the same interface, and the proxy would maintain a reference to the real service.


## Structure

<div class="imgcols">
<img alt="dp-proxy" src="/assets/dp-proxy.svg">
<img alt="dp-proxy-process" src="/assets/dp-proxy_001.svg">
</div>

Proxies come in different flavors:

- **Remote** proxy (aka. _Ambassador_): Provides local proxy to remote object (different process or physical location)
- **Virtual** proxy: Creates expensive objects on demand; not to be confused with singleton (unique instance)
- **Protection** Proxy: controls access to the original object, e.g. read-only access that simulates write.

> Note: A **smart reference** is a proxy, too: it behaves just like the underlying object, but manages the state of the instance.


## Examples

- Caching for network requests
- Log output routing
- Lazy initialization of expensive objects
- Related: security facade; behaves like proxy, but hides error handling or authentication


## Proxy, Decorator and Composite

Proxy, [Decorator](/03ln-inheritance/) and [Composite](/07ln-iterator-composite-observer/) pattern have a similar structure using recursive composition.
However, the point of the 
- _decorator_ is to _add functionality without subclassing_: one enclosed instance plus extra logic;
- _composite_ is to model a recursive structure, such as user interface widgets: arbitrary number of enclosed instances, logic typically restricted to traversing the structure or specific to leaf classes;
- _proxy_ is to mimic the original object (!) while adding access control or caching.

> Note: In edge cases, a proxy actually behaves like a decorator (or vice versa).
> However, decorators can typically be stacked, often in arbitrary order;
> Proxy hierarchies are typically very flat: either there is a proxy, or there is none.


---

# Adapter

Let's stick with the example above, where you implemented a `MensaService` class that allows you to get the list of meals via `getMeals(String date)`.
Now you meet with your friend who is writing the front-end part of the app.
Well, they were expecting you to provide the meals in form of an `Iterable` where you can set the date:

```java
interface MealProvider extends Iterable<Meal> {
	void setDate(String date);
	// Iterator<Meal> iterator();  <-- from Iterable!
}
```

Doh.
This is quite a different interface, but there is no way that either of you changes their code -- think of all the refactoring of the unit tests etc.!

This is where the _adapter_ pattern comes in.
Just like you use adapters for tools if they don't fit, you can create an adapter that fits both ends:

```java
class MealAdapter extends MensaService implements MealProvider {
	private String date;
	
	@Override
	public void setDate(String date) { this.date = date; }
	
	@Override
	public Iterator<Meal> iterator() {
		try {
			// optionally: use today if date == null?
			return super.getMeals(date).iterator();
		} catch (IOException e) {
			return Collections.emptyIterator();
		}
	}
}
```

Voila, this is your _class adapter_:

```java
MealProvider mp = new MealAdapter();
mp.setDate("20171206");
for (Meal m : mp)
	System.out.println(m);
```

Alternatively, you could write an _object adapter_, that implements the target interface, but maintains a reference to an instance of the class to be adapted (see structure below).


## Structure

_Class_ adapter:

![dp-adapter-class](/assets/dp-adapter.svg)

_Object_ adapter:

![dp-adapter-object](/assets/dp-adapter_001.svg)


> Note: The Adapter is not to be confused with the [Facade](https://en.wikipedia.org/wiki/Facade_pattern), in which a whole subsystem is abstracted into a new class, typically implementing a **new** interface.
> An example for a Facade would be to couple the classes `Engine`, `Transmission` and `Starter` into the class `Auto`, which adds the logic on how to start, drive and stop.


## Examples

- `ArrayAdapter` in Android to render data arrays in views
- Wrappers for third-party libraries
- _Object_ adapter often best choice if implementation of Adaptee unknown

---


# Flyweight

Consider the following example: you want to build a "text based" web browser (e.g. for visually impaired).
Here is a simple page that contains a list of a few images.

```html
<ul>
	<li><img alt="Exhibit 1" src="picasso.png"></li>
	<li><img alt="Exhibit 2" src="vangogh.png"></li>
	<li><img alt="Exhibit 3" src="munch.png"></li>
	<li><img alt="Exhibit 4" src="monet.png"></li>
</ul>
```

In Java, we could use an `Img` class to represent each image:

```java
class Img {
	final Image image;
	final String caption;

	Img(String caption, String path) throws IOException, URISyntaxException {
		this.caption = caption;

		// get resource file uri
		File file = new File(getClass().getClassLoader()
				.getResource(path).toURI());

		// load image; use helper function from JavaX package
		// https://docs.oracle.com/javase/8/docs/api/javax/imageio/ImageIO.html
		this.image = ImageIO.read(file);
	}

	void describe(PrintStream ps) {
		ps.println(String.format("%s: %d x %d",
				caption,
				image.getHeight(null),
				image.getWidth(null)
		));
	}
}
```

You could now instantiate the list with a few image tags and print them to make it a text based browser. 

```java
List<Img> items = new LinkedList<>();

// allocate items
items.add(new Img("Exhibit 1", "picasso.png"));
items.add(new Img("Exhibit 2", "vangogh.png"));
items.add(new Img("Exhibit 3", "munch.png"));
items.add(new Img("Exhibit 4", "monet.png"));

// print them out
for (Img e : items)
	e.describe(System.out);
```

This works alright as long as every image is different, but is fairly inefficient if images are displayed multiple times:

```html
<ul>
	<li><img alt="Exhibit 1" src="picasso.png"></li>
	<li><img alt="Also Picasso" src="picasso.png"></li>
	<li><img alt="Picasso, too" src="picasso.png"></li>
	<li><img alt="Oh look, Picasso" src="picasso.png"></li>
</ul>
```

> This may sound hypothetical, but think of recurring images in an endless scroll page such as the "Like" button on Facebook.

Clearly, re-loading the `picasso.png` is not only inefficient in terms of load times and network traffic, it also has bad effect on memory.

This is where the _Flyweight_ pattern comes into play.
The general idea is to separate **intrinsic** (static, unchanged; here: `picasso.png`) information from **extrinsic** (variable; here: `alt` caption) information.

The intrinsic share becomes the _flyweight_, and it will be shared among all different `img` that have the same `src`.

```java
class Flyweight {
	// intrinsic state
	private final Image image;

	Flyweight(String path) throws URISyntaxException, IOException {
		// get resource file uri
		File file = new File(getClass().getClassLoader()
				.getResource(path).toURI());

		// load image (the intrinsic state)
		this.image = ImageIO.read(file);
	}

	void describe(PrintStream ps, Img es) {
		ps.println(String.format("%s: %d x %d",
				es.caption,
				image.getHeight(null),
				image.getWidth(null)
		));
	}
}
```

These flyweights are managed by a factory; that is, the user never allocates a flyweight manually, but retrieves instances from the factory, which facilitates the sharing.

```java
class FlyweightFactory {
	private Map<String, Flyweight> flyweights = new HashMap<>();

	Flyweight getFlyweight(String path) throws URISyntaxException, IOException {
		if (flyweights.containsKey(path))
			return flyweights.get(path);

		// allocate new flyweight
		Flyweight fw = new Flyweight(path);
		flyweights.put(path, fw);

		return fw;
	}
}
```

The extrinsic share becomes the new `Img` class; it will have individual `alt` captions, but maintain references to the shared flyweight.

```java
class Img {
	final String caption;
	final Flyweight flyweight;  // reference!

	Img(String caption, Flyweight flyweight) {
		this.caption = caption;
		this.flyweight = flyweight;
	}

	void describe(PrintStream ps) {
		// inject extrinsic state to flyweight
		flyweight.describe(ps, this);
	}
}
```

Back to the original example, our text browser.
Instead of allocating the `Img` tags

```java
List<Img> items = new LinkedList<>();
FlyweightFactory factory = new FlyweightFactory();

// allocate items
items.add(new Img("Exhibit 1", factory.getFlyweight("picasso.png")));
items.add(new Img("Also Picasso", factory.getFlyweight("picasso.png")));
items.add(new Img("Picasso, too", factory.getFlyweight("picasso.png")));
items.add(new Img("Oh look, Picasso", factory.getFlyweight("picasso.png")));

// print them out
for (Img e : items)
	e.describe(System.out);
```

This way, the `picasso.png` is only loaded once and then shared among all the other `Img` instances.
As a result: the application is faster (single loading) and needs less memory (all static data just once).
You can easily try it by loading a few hundreds of images: you will see how much faster (and less memory) the flyweight uses.


## Structure

![dp-flyweight](/assets/dp-flyweight.svg)

Notes: 
- The term _flyweight_ is misleading: it is _light_ in a sense of _less and static parts_, but often contains the "memory heavy" objects.
- In a variant, there is no `operation()` but just a shared reference to the flyweights, which act as a pool of "heavy" objects.


## Examples

- Glyph (letter) rendering for text fields; intrinsic state: true-type fonts (often several MB), extrinsic state: position on screen, scale (size).
- Browser rendering the same media multiple times; intrinsic state: actual media (image, video, audio), extrinsic state: location on screen
- Android `RecyclerView`; intrinsic state: inflated layout of `RecycleView`, extrinsic state: actual contents to be displayed (often nested with further Flyweight)
- Video games rendering/tiling engines; intrinsic state: actual texture or tile, extrinsic state: 3D location and orientation

---

# Design Patterns Summary

There is a total of 23 design patterns described by Gamma _et al._
Throughout this course, we already discussed quite a few of those:

## Creational Patterns
- [Factory and factory method](/08ln-singleton-factory-strategy-command/): Provide an interface for creating families of related or dependent objects without specifying their concrete class.
- [Singleton](/08ln-singleton-factory-strategy-command/): Guarantee _unique_ instance of class, and provide global access.

## Structural Patterns
- [Adapter](#adapter): Make a piece of software fit your needs.
- [Composite](/07ln-iterator-composite-observer/): Recursive data structure with containers and leaves, to represent part-whole hierarchies; composite lets client treeat objects and compositions uniformly.
- [Decorator](/03ln-inheritance): Add functionality to objects without changing their basic interface.
- [Flyweight](#flyweight): Share common data to support large numbers of similar objects.
- [Proxy](#proxy): Provide a surrogate to allow caching or access control; indistinguishable to the client (same interface).

## Behavioral Patterns
- [Command](/08ln-singleton-factory-strategy-command/): create objects that can do or undo certain actions; use it to realize undo, macros and transactions.
- [Iterator](/07ln-iterator-composite-observer/): Provide access to elements of aggregate without exposing the underlying structure/representation.
- [Observer](/07ln-iterator-composite-observer/): Subscribe to an object to get notified on state change.
- [State](https://github.com/hsro-inf-prg3/03-inheritance): Allow an object to alter its behavior when its internal state changes; objects will appear to change their class.
- [Strategy](/08ln-singleton-factory-strategy-command/): Define family of algorithms and make them interchangeable.
- [Template method](/03ln-inheritance/): Define skeleton of algorithm/functionality in an operation, deferring certain steps/parts to subclasses.


<p style="text-align: right">&#8718;</p>
