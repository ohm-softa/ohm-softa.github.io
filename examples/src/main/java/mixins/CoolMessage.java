package mixins;

public class CoolMessage implements Escalatable, Unicodable {
	private String m;

	CoolMessage(String m) {
		this.m = m;
	}

	@Override
	public String text() {
		return m;
	}

	public static void main(String[] args) {
		CoolMessage cm = new CoolMessage("\uD83D\uDE08");

		System.out.println(cm.text());
		System.out.println(cm.escalated());
		System.out.println(cm.escalated());
		System.out.println(cm.escalated());

		CoolMessage cm2 = new CoolMessage("meh");
		System.out.println(cm2.escalated());
		System.out.println(cm2.escalated());
		System.out.println(cm.utf8());
	}
}
