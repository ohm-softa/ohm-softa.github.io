package generics;

public class SimpleMapImpl implements SimpleMap {
	private class Entry {
		Entry(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
		Object key;
		Object value;
		Entry next;
	}

	private Entry head;

	@Override
	public void put(Object key, Object value) {
		if (head == null) {
			head = new Entry(key, value);
			return;
		}

		Entry it = head, prev = null;
		while (it!= null) {
			if (it.key.equals(key)) {
				it.value = value;
				return;
			}
			prev = it;
			it = it.next;
		}

		prev.next = new Entry(key, value);
	}

	@Override
	public Object get(Object key) {
		Entry it = head;
		while (it != null) {
			if (it.key.equals(key))
				return it.value;
			it = it.next;
		}

		return null;
	}

	public static void main(String[] args) {
		SimpleMap map = new SimpleMapImpl();
		map.put("Schleichmichl", "DROP TABLE matrikel");
		Integer schleichmichl = (Integer) map.get("Schleichmichl");
		System.out.println(schleichmichl);
	}
}
