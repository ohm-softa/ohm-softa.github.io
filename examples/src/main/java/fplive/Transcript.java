package fplive;

import java.util.Iterator;
import java.util.LinkedList;

public class Transcript implements Iterable<Record> {
	java.util.List<Record> records = new LinkedList<>();

	@Override
	public Iterator<Record> iterator() {
		return records.iterator();
	}

	public String toString() {
		return records.toString();
	}
}
