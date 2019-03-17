package designpattern.decorator;

import org.junit.jupiter.api.Test;

class PayloadTest {
	@Test
	void testPayloads() {
		Payload raw = new TextPayload("I'm plain text");
		System.out.println(raw.getText());
	}

	@Test
	void testGzipPayload() {
		Payload text = new TextPayload("some deflated text");
		GzipPayload inflated = new GzipPayload(text);

		System.out.println(inflated.getDeflatedText());
		System.out.println(inflated.getText());
	}

	@Test
	void testDecryptedPayload() {
		Payload text = new TextPayload("some deflated and encrypted text");
		Payload inflated = new GzipPayload(text);
		Payload decrypted = new EncryptedPayload(inflated);

		System.out.println(text.getText());
		System.out.println(inflated.getText());
		System.out.println(decrypted.getText());
	}
}