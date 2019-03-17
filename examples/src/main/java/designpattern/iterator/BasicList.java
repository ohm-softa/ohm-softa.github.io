package designpattern.iterator;

public interface BasicList<T> extends Iterable<T> {
	T get(int i);
	void add(T value);
	int size();
}
