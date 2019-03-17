package mixins;

import java.util.stream.Stream;

public interface StatefulEscalate2 extends Stateful {
	String text();

	default String escalated() {
		int n = getState(StatefulEscalate2.class, 0);
		setState(StatefulEscalate2.class, n+1);

		// generate n bangs, or empty strings for n=0
		String bangs = Stream.generate(() -> "!")
				.limit(n)
				.reduce("", (a, b) -> a + b);

		return text().toUpperCase() + bangs;

	}
}
