package fplive;


import com.google.gson.annotations.SerializedName;

public class Record {
	private String name;
	private int grade;

	Record(String n, int g) {
		this.name = n;
		this.grade = g;
	}

	public String getName() {
		return name;
	}

	public int getGrade() {
		return grade;
	}

	public String toString() {
		return name + " (" + grade + ")";
	}
}
