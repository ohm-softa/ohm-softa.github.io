package designpattern.decorator;

public class GzipPayload extends PayloadDecorator {
	public GzipPayload(Payload deflated) {
		super(deflated);
	}
	public String getText() {
		String balloon = getSource().getText();
		return "inflate(" + balloon + ")";
	}
	public String getDeflatedText() {
		return getSource().getText();
	}
}
