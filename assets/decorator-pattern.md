```
@startuml

abstract class Payload {
	+abstract getText(): String
}

class TextPayload extends Payload {
	-text: String
	+TextPayload(t: String)
	+getText(): String
}

abstract class PayloadDecorator extends Payload {
	-source: Payload
	+PayloadDecorator(p: Payload)
	#getSource(): Payload
}

class EncryptedPayload extends PayloadDecorator {
	+EncryptedPayload(p: Payload)
	+getText(): String
}

class GzipPayload extends PayloadDecorator {
	+GzipPayload(p: Payload)
	+getText(): String
}

PayloadDecorator *-- Payload

@enduml
```