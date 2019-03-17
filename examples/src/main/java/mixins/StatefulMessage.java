package mixins;


public class StatefulMessage extends StatefulObject
		implements StatefulEscalate2, StatefulConfusable {
	private String m;

	public StatefulMessage(String m) {
		this.m = m;
	}

	public String text() {
		return m;
	}

	public static void main(String[] args) {
		StatefulMessage m1 = new StatefulMessage("Hans");
		StatefulMessage m2 = new StatefulMessage("Dampf");

		System.out.println(m1.escalated());
		System.out.println(m1.escalated());
		System.out.println(m1.escalated());
		System.out.println(m2.escalated());
		System.out.println(m2.escalated());
		System.out.println(m2.escalated());

		System.out.println(m1.confuse());
		System.out.println(m1.confuse());
		System.out.println(m2.confuse());
	}
}
