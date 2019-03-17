package designpattern.factory;

public class JsonLeaf implements Leaf {
	private final String name, value;
	public JsonLeaf(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return '"' + name + "\": \"" + value + '"';
	}
}
