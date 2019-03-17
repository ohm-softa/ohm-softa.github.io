package fp;

class List<T> {
	final T head;
	final List<T> tail;

	protected List(T el, List<T> tail) {
		this.head = el;
		this.tail = tail;
	}

	boolean isEmpty() {
		return head == null;
	}

	static <T> List<T> empty() {
		return new List<T>(null, null);
	}

	static <T> List<T> list(T elem, List<T> xs) {
		return new List<>(elem, xs);
	}

	static <T> List<T> list(T... elements) {
		if (elements.length == 0)
			return empty();
		int i = elements.length - 1;
		List<T> xs = list(elements[i], empty());
		while (--i >= 0)
			xs = list(elements[i], xs);
		return xs;
	}

	@Override
	public String toString() {
		if (isEmpty()) return "nil";
		else return "(" + head + " " + tail + ")";
	}
}
