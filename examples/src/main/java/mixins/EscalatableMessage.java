package mixins;

public class EscalatableMessage extends Message {
	public String escalated() {
		return text().toUpperCase();
	}
}
