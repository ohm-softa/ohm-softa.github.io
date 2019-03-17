package designpattern.proxyadapter;

import java.util.List;

public class Meal {
	String name;
	List<String> notes;
	public String toString() {
		return name + (notes.size() > 0 ? " (...)" : "");
	}
}
