package fplive;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ImperativeFunctional {
	public static void main(String[] args) {
		// imperativ
		for (Student s : Database.getStudents()) {
			if (s.getClasses().contains("Programmieren 3")) {
				Transcript tr = Database.getToR(s.getMatrikel());
				for (Record r : tr)
					// ps1.println(r);
					System.out.println(r);
			}
		}

		// funktional
		Database.getStudents().stream()
				.filter(s -> s.getClasses().contains("Programmieren 3"))
				.map(Student::getMatrikel)
				.map(Database::getToR)
				.flatMap(t -> t.records.stream())
				.forEach(System.out::println);
	}
}
