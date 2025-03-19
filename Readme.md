# Software Architecture


_Required class for [CS majors](https://www.th-nuernberg.de/fakultaeten/in/studium/bachelorstudiengang-informatik/) at the [Technische Hochschule Nürnberg](https://www.th-nuernberg.de). --- Pflichtmodul im [Bachelorstudiengang Informatik](https://www.th-nuernberg.de/fakultaeten/in/studium/bachelorstudiengang-informatik/) an der [Technischen Hochschule Nürnberg](https://www.th-nuernberg.de)._

## Class Schedule

**Lecture**: Mondays at **9.45a** in HW.307, starting Mar 24: Class, mostly live coding (interaction appreciated :-).

**Tutorials**: We'll work on the assignments based on the preceding week's lecture, so bring your laptop! Tutorials start Mar 25.

- Tuesdays at *9.45a* in SP.467 (Riedhammer & Tallowitz)
- Tuesdays at *2p* in SP.467/468 (Braun & Tallowitz)

**Announcements and Discussions:** Teams, enrollment key: `b1f92iq`

_Note: Materials will be in English, the lectures/tutorials will be taught in German; the written exam will be German (you may answer in English)._

## Class and Credits (_Leistungsnachweis_)

**Lectures**: Not your classic lecture--- we'll work together on concrete problems and their solution. 
The class will be taught in German, the materials are mostly in English.

**Tutorials and assignments**: Pair programming preferred, [_BYOD_](https://en.wikipedia.org/wiki/Bring_your_own_device) strongly recommended; _nota bene: assignments typically take longer than 90' to complete!_

**Credits**: written exam (90') at the end of the semester.

## Recommended Textbooks

- Bloch: [Effective Java](https://www.amazon.de/Effective-Java-2nd-Programming-Language/dp/0321356683/)
- Oaks: [Java Performance](https://www.amazon.de/Java-Performance-The-Definitive-Guide/dp/1449358454/)
- Gamma _et al._: [Design Patterns](https://www.amazon.de/Patterns-Elements-Reusable-Object-Oriented-Software/dp/0201633612/)
- Subramaniam: [Functional Programming in Java](https://www.amazon.de/Functional-Programming-Java-Harnessing-Expressions/dp/1937785467/)
- Siedersleben: [Moderne Softwarearchitektur](https://www.amazon.de/Moderne-Software-Architektur-Umsichtig-planen-robust/dp/3898642925/)


### Additional Materials

See `General` Channel on Teams > Files.

## Syllabus

| #      | Chapter | Materials | Description |
|-----------|-------------|----------|-------------|
| 1    | Introduction | [slides](/01s-intro/), [assignments](https://github.com/ohm-softa/01-tools/) | We talk about software architecture, abstraction, decomposition and good software design. We also review the tools and resources that you'll need for this class. |
| 2    | Classes and Interfaces Revisited | [slides](/02s-classes-interfaces/), [lecture notes](/02ln-classes-interfaces/), [assignments](https://github.com/ohm-softa/02-classes-interfaces/) | We look at different types of classes (inner, anonymous, local, static), when to use them, and which visibility for which purpose. Also: `@FunctionalInterface` and lambda expressions. |
| 3   | Inheritance Revisited | [slides](/03s-inheritance/), [lecture notes](/03ln-inheritance/), [assignments](https://github.com/ohm-softa/03-inheritance) | We talk about abstract and final classes, (pure) virtual functions and defaults. Also, when (and how) to use abstract base classes, and how the Decorator pattern can be used to add functionality to existing classes. |
| 4     | Mixins, pt. 1; Generics, pt. 1 | [slides](/04s-generics-1/), [lecture notes](/04ln-generics-1/), [assignments](https://github.com/ohm-softa/04-generics) | After a short digression to Mixins, we dig into the details of how generics work in Java, and how to apply them to data structures and algorithms. |
| 5    | Mixins, pt. 2; Generics, pt. 2 | [slides](/05s-generics-2/), [lecture notes](/05ln-generics-2/), [assignments](https://github.com/ohm-softa/05-generics-bounds) | We'll review Mixins and see how to use generics to make them stateful. Generics and inheritance need special attention, and will lead us to bounds and wildcards. |
| 6    | Reflection and Annotations | [slides](/06s-reflection-annotations/), [lecture notes](/06ln-reflection-annotations/), [assignments](https://github.com/ohm-softa/06-annotations-reflection) | Learn how reflection works in Java, and how they enable annotations by using examples of testing ([JUnit5](http://junit.org/junit5/)), serialization ([gson](https://github.com/google/gson)) and networking ([retrofit](https://github.com/square/retrofit)). |
| 7 | Design Patterns, pt. 1 | [slides](/07s-iterator-composite-observer/), [lecture notes](/07ln-iterator-composite-observer/), assignments: [JavaFX (recommended)](https://github.com/ohm-softa/07-composite-observer-jfx) or [Android (advanced)](https://github.com/ohm-softa/07-composite-observer-android) | We begin with a few basic patterns: composite, iterator and observer, and use that to dive into Android and MVC/MVVC. |
| 8 | Design Patterns, pt. 2 | [slides](/08s-singleton-factory-strategy-command/), [lecture notes](/08ln-singleton-factory-strategy-command/), assignments: [JavaFX](https://github.com/ohm-softa/08-singleton-factory-strategy-jfx) or [Android](https://github.com/ohm-softa/08-singleton-factory-strategy-android) | We look at more every-day-patterns: singleton, factory, strategy and command. |
| 9 | Design Patterns, pt. 3 | [slides](/09s-proxy-adapter-flyweight/), [lecture notes](/09ln-proxy-adapter-flyweight/), assignments: [JavaFX](https://github.com/ohm-softa/09-adapter-flyweight-jfx), [Android](https://github.com/ohm-softa/09-adapter-flyweight-android) | We round up a few more useful patterns: proxy and adapter to make other peoples' modules fit your needs, and flyweight to save on precious memory in (mostly) graphical apps. |
| 10 | Parallel Processing | [slides](/10s-threads/), [lecture notes](/10ln-threads/), assignments: [JavaFX](https://github.com/ohm-softa/10-threads-jfx), [Android](https://github.com/ohm-softa/10-threads-android) | Because sometimes, you need to work on more than one thing at a time! We'll look at threads and how to control and synchronize them. |
| 11 | Asynchronous Programming | [slides](/11s-futures/), [lecture notes](/11ln-futures/), assignments: [CLI](https://github.com/ohm-softa/11-futures-cli), [Android](https://github.com/ohm-softa/11-futures-android) | User interfaces often require us to work not only in parallel, but to do so in an asynchronous way. Learn about a better `Future` for asynchronous or concurrent workloads, and what _promise chaining_ can do for you. |
| 12 | Introduction to Functional Programming | [slides](/12s-fp1/), [lecture notes](/12ln-fp1/), [assignments](https://github.com/ohm-softa/12-functional-cli) | Leave your imperative and objected oriented programming comfort zone and follow me down the rabbit hole of functional programming. After some theory, we'll do some basic exercises, including `filter`, `map` and `forEach`. |
| 13 | Functional Programming in Java | [slides](/13s-fp2/), [lecture notes](/13ln-fp2/), [assignments](https://github.com/ohm-softa/13-map-reduce-collect) | We'll talk about the specifics (and limits) of functional programming in Java. Learn about the classes and interfaces used for Java's functional parts, and the more sophisticated stream reduction using `reduce` and `collect`. |
| 14 | Patterns of Modern Software Architecture | [slides](/15s-patterns-of-modern-software-architecture/) | We're wrapping up the semester by talking about patterns of modern software architecture, such as dependency injection or microservices. |




_Subscribe to [https://github.com/ohm-softa/ohm-softa.github.io](https://github.com/ohm-softa/ohm-softa.github.io) repository to follow updates._
