package designpattern.factory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonComposite implements Composite {
	private final String name;
	private List<Component> components = new LinkedList<>();

	public JsonComposite(String name) {
		this.name = name;
	}

	@Override
	public void add(Component c) {
		components.add(c);
	}

	@Override
	public String toString() {
		return "\"" + name + "\": {" + String.join(", ", components.stream().map(o -> o.toString()).collect(Collectors.toList())) + "}";
	}
}
