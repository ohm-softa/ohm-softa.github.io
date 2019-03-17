package fplive;

import java.util.Collections;

public class Student {
	private int matrikel;
	private String name;
	private java.util.List<String> classes;

	public Student(int matrikel, String name) {
		this.matrikel = matrikel;
		this.name = name;
	}

	public int getMatrikel() {
		return matrikel;
	}

	public String getName() {
		return name;
	}

	public java.util.List<String> getClasses() {
		return Collections.unmodifiableList(classes);
	}

	public String toString() {
		return name + " (" + matrikel + ")";
	}
}
