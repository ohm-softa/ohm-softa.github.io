package generics;


public class SortedMapImpl<K extends Comparable<K>, V> implements Map<K, V> {
	class Element {
		K key;
		V value;
		Element(K key, V value) {
			this.key = key;
			this.value = value;
		}
		Element left, right;
	}

	Element root;

	@Override
	public void put(K key, V value) {
		if (root == null) {
			root = new Element(key, value);
			return;
		}

		Element it = root;
		while (it != null) {
			int c = key.compareTo(it.key);
			if (c == 0) {
				it.value = value;
				return;
			} else if (c < 0) {
				if (it.left == null) {
					it.left = new Element(key, value);
					return;
				} else {
					it = it.left;
				}
			} else {
				if (it.right == null) {
					it.right = new Element(key, value);
					return;
				} else {
					it = it.right;
				}
			}
		}
	}

	@Override
	public V get(K key) {
		if (root == null)
			return null;

		Element it = root;
		while (it != null) {
			int c = key.compareTo(it.key);
			if (c == 0) return it.value;
			else if (c < 0) it = it.left;
			else it = it.right;
		}

		return null;
	}

	public static void main(String[] args) {
		class Klass {
			int id;
			Klass(int id) { this.id = id; }
		}


	}
}
