package designpattern.decorator;

public class EncryptedPayload extends PayloadDecorator {
	public EncryptedPayload(Payload encrypted) {
		super(encrypted);
	}

	public String getText() {
		String cipher = getSource().getText();
		return "decrypt(" + cipher + ")";
	}

	public String getEncryptedText() {
		return getSource().getText();
	}
}
