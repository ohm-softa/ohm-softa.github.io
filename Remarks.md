# Überarbeiteter Inhalt zum WS 2017

https://web.hypothes.is/



- **Parallel and asynchronous processing (Nov 27, [slides](/14s-parallel-async/), [lecture notes](/14ln-parallel-async/), [assignments](https://github.com/ohm-softa/11-futures-cli))**

	Because sometimes, you need to work on more than one thing at a time!
	Learn about a better `Future` for asynchronous or concurrent workloads, and what _promise chaining_ can do for you.

## Zu den Übungen
Minimale Vorgaben, wenig technische Erklärungen. Stattdessen: Hinführende Links, Suchbegriffe, Tips. --- Vielleicht können wir so die Selbstständigkeit erhöhen?

Teamarbeit: Pairprogramming gewünscht; idealerweise am eigenen Rechner.

## Modulplan
1. Introduction
	Handswerkszeug (Git, IntelliJ, Google, SO, gradle, JUnit).

	**Übung:** Git Fingerübung mit fork-clone-branch-push-PR, einzelnem Testcase.
2. Classes and interfaces.
	Inner, anonymous, local, static, and when to use them.
	Visibility (public-private-protected-package).
	Also: `@FunctionalInterface` and Lambda Expressions.
3. Inheritance revisited.
	abstract, final, default; Concrete examples for when (and how) to use abstract base classes.
4. Generics, part 1.
	Gerneric classes and methods; data structures and sorting.
5. Generics, part 2.
	Generics and inheritance; bounds and wildcards
6. Annotations and Reflection.
	With concrete examples of serialization (gson) and network (retrofit)
7. Design patterns, part 1.
	MVC/MVVC and intro to Android.
	Composite, iterator, observer.
	**Assignments:** Android!
8. Design patterns, part 2.
	Singleton, factory, strategy, visitor.
9. Design patterns, part 3.
	Proxy, adapter, flyweight.
10. Threading, part 1.
	Threads and runnables, consumer-producer-problem.
11. Threading, part 2.
	`Future` und `CompletableFuture`, promise chaining.
12. Functional programming, part 1.
	- Immutability
	- Lists and Sets
	- Basic concepts: filter, map, reduce.
13. Functional programming, part 2.
	- `filter`-`map`-`reduce` (`Predicate`, `Function`, `BinaryOperator`)
	- `Optional`
	- `collect`, `groupingBy`, etc.
14. Questions.


## Toolbox
- IntelliJ
- Gradle build system
- Git

## Generics
- Wiedereinstieg aus dem 2. Semester: generische Klassen und Methoden
- Bounds und Wildcards
- Generics und Vererbung

## Klassen, Vererbung und erweiterte Sprachfeatures
Nochmal mit Wolfgang/Prg2 abzustimmen.
- Klassen, Interfaces, abstrakte Klassen (Beispiel state machine)
- Interfaces und Factories
- Innere Klassen: Sichtbarkeiten (private, public, package, static)
- FunctionalInterfaces und Lambda
- `default`-Methoden
- Reflection und Annotationen

## Design Patterns
- Iterator
- Factory
- Flyweight
- Composite
- Strategy
- Observer
- Singleton
- Proxy und Adapter
- Visitor

_Weitere optionale Patterns:_

- Data Transfer Objets
- Repository
- Model View Controller/Model View ViewModel

## Fortgeschrittene Aspekte der Softwarearchitektur

_Sollten schon gemacht werden, da sie im weiteren Studium als gegeben betrachtet werden, vielleicht aber keine extra Übung sondern nur Artikel/Buch?_

- Enums
- Exceptions: Wann behandle ich was

_Soll das raus? Vielleicht eine Artikelsammlung bzw. Buchkapitel zum selbststudium? Oder durch eine gute Übung?_
- Komponenten und Schnittstellen
- Daten vs. Entitätstypen
- Enge und lose Kopplung
- Android GUI
- Dependency Injection

_Einführung durch Artikelsammlung in die einzelnen Themen und Anwendung mit Übung z.B. in ersten 2-3 Mini-PStAs Komponenten bauen zum Abrufen u. Konvertieren von Daten mit Hilfe von Resteasy, dann Komponenten in .jar-Dateien packen und in Android App dann mit Gradle einbinden? Dadurch würden sich auf jeden Fall Komponenten, Interfaces, Factory, DTOs, Repositories, ggf. Proxy-Pattern, Entitätstypen und Exceptions ergeben?_

## Nebenläufigkeit
- Threads und Runnable
- Synchronisation mit `join`, `synchronized`, `wait` und `notify`
- `Future` und `CompletableFuture`: Promises and Promise Chaining


## Funktionale Programmierung (Januarwochen)
- Immutability
- Einfache Beispiele (z.B. Mergesort mit immutable list)
- Java Streams: `forEach`, `filter`, `map`, `reduce`; `Optional`
- Set Comprehension mit `Stream.generate()`?
- Lazy Evaluation und `.parallel()`
- Wichtige `FunctionalInterface`s: `Predicate`, `Function`, `BinaryOperator`, ...
- Lambda und Methodenreferenzen (insb. Konstruktor)
- Erweiterte Funktionen: `collect`, `groupingBy`
- Übung evtl. mit Hibernate um Streams auf einer Datenbank anzuwenden? Ggf. mit Herr Höfig bzw. Herr Krüger absprechen ob die Studenten noch JDBC machen in den Übungen?


- Generics raus? Oder kompakter?
- DI?


TODO: Liskovsches Substitutionsprinzip (--> Kap 3)


# Sommersemester 2019 (THN)

- Generics 1 viel zu kurz; mit 2 verbinden und Mixins separat mit Decorator?
- Mehr inhalte aus "Clean Architecture"?
- DI und RxJava in das letzte Kapitel
- try-catch-final (Java/C#) vs. RAII (C++) Beispiel mit Guards?
- Repository pattern

# Sommersemester 2023

- Singleton in DP/1 vorziehen, dadurch mehr platz für Strategy und Factory
