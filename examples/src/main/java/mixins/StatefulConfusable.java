package mixins;

public interface StatefulConfusable extends Stateful {
	String text();

	default String confuse() {
		// no more typecasts!
		String s = getState(StatefulConfusable.class, "?");

		setState(StatefulConfusable.class, s + s);

		return text() + s;
	}
}
