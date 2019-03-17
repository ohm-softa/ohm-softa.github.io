package javafp;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
	public static void main(String[] args) {
		List<Integer> list = new LinkedList<>();
		Stream.of(1, 3, 3, 7).forEach(i -> list.add(i));
		System.out.println(list);

		System.out.println(Stream.of(1, 3, 3, 7).collect(Collectors.toList()));
	}
}
