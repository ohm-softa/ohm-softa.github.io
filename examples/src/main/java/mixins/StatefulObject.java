package mixins;

import java.util.HashMap;
import java.util.WeakHashMap;

public class StatefulObject implements Stateful {
	private HashMap<Class, Object> states
			= new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")  // shhh... :-)
	public final <T> T getState(Class<?> clazz, T initial) {
		// cast necessary, since internally we store Object!
		return (T) states.getOrDefault(clazz, initial);
	}

	@Override
	public final <T> void setState(Class<?> clazz, T s) {
		states.put(clazz, s);
	}
}
