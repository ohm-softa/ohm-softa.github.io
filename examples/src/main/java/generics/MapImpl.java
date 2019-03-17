package generics;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MapImpl<K, V> implements Map<K, V> {
	class Entry {
		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		K key;
		V value;
		Entry next;
	}

	Entry head;

	@Override
	public void put(K key, V value) {
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
	public V get(K key) {
		Entry it = head;
		while (it != null) {
			if (it.key.equals(key))
				return it.value;
			it = it.next;
		}

		return null;
	}

	static <T> void blub(Class<T> clazz) {
		T[] arr = (T[]) Array.newInstance(clazz, 10);

		System.out.println(Arrays.toString(arr));
	}

	public static void main(String[] args) {
		MapImpl<String, Integer> map = new MapImpl<>();
		map.put("Hans", 14235);
		System.out.println("Hans: " + map.get("Hans"));

		blub(String.class);

		//map.put("Peter", "Willi");  // compiler error!
		System.out.println("Peter: " + map.get("Peter"));
	}
}
