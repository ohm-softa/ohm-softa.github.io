package designpattern.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SimpleList<T> implements BasicList<T> {
	private class Element {
		Element(T value) {
			this.value = value;
		}
		Element next;
		T value;
	}

	private Element root;
	private int size;

	@SafeVarargs
	public SimpleList(T... values) {
		Element prev = null;
		for (T v : values) {
			if (prev == null) {
				root = prev = new Element(v);
			} else {
				prev.next = new Element(v);
				prev = prev.next;
			}
		}
		size = values.length;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void add(T value) {
		if (root == null) {
			root = new Element(value);
			size = 1;
			return;
		}

		Element it = root;
		while (it.next != null)
			it = it.next;

		it.next = new Element(value);
		size = size + 1;
	}

	@Override
	public T get(int i) {
		if (root == null)
			throw new NoSuchElementException();

		Element it = root;
		while (i-- > 0) {
			it = it.next;

			if (it == null)
				throw new NoSuchElementException();
		}

		return it.value;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Element it = root;
			@Override
			public boolean hasNext() {
				return it == null;
			}

			@Override
			public T next() {
				T value = it.value;
				it = it.next;
				return value;
			}
		};
	}
}
