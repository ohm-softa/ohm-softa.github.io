package fplive;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Database {
	private static java.util.List<Student> students = new java.util.LinkedList<>();
	private static Map<Integer, Transcript> tors = new HashMap<>();

	private static class Helper {
		@SerializedName("m")
		int matrikel;
		@SerializedName("n")
		String name;
		@SerializedName("g")
		int grade;
	}

	static {
		{
			// deserialize students
			InputStreamReader reader = new InputStreamReader(Database.class.getClassLoader().getResourceAsStream("students.json"));
			Type type = new TypeToken<java.util.List<Student>>() {}.getType();
			students = new Gson().fromJson(reader, type);
		}

		{
			// deserialize transcripts and add to individual records
			InputStreamReader reader = new InputStreamReader(Database.class.getClassLoader().getResourceAsStream("tors.json"));
			Type type = new TypeToken<java.util.List<Helper>>() {}.getType();
			java.util.List<Helper> hs = new Gson().fromJson(reader, type);
			for (Helper h : hs) {
				Transcript t = new Transcript();
				if (tors.containsKey(h.matrikel))
					t = tors.get(h.matrikel);
				else
					tors.put(h.matrikel, t);

				t.records.add(new Record(h.name, h.grade));
			}
		}
	}

	public static java.util.List<Student> getStudents() {
		return Collections.unmodifiableList(students);
	}

	public static Transcript getToR(int matrikel) {
		return tors.get(matrikel);
	}
}
