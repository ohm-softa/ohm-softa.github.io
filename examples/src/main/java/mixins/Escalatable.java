package mixins;

import java.util.WeakHashMap;
import java.util.stream.Stream;

public interface Escalatable {
	String text();

	default String escalated() {
		return text().toUpperCase();
	}
}
