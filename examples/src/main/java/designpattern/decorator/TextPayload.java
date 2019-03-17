package designpattern.decorator;

public class TextPayload extends Payload {
	private String text;
	public TextPayload(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
