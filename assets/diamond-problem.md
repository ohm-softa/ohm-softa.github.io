```
@startuml
interface Top {
	default void method();
}

interface Left extends Top {
	default void method();
}

interface Right extends Top {
	default void method();
}

class Bottom implements Left, Right {
	+void method();
}
@enduml
```