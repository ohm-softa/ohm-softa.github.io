package designpattern.decorator;

public abstract class PayloadDecorator extends Payload {
	private Payload source;
	protected Payload getSource() {
		return source;
	}
	protected PayloadDecorator(Payload p) {
		source = p;
	}
}
