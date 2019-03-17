package designpattern.factory;

public class XmlLeaf implements Leaf {
	private final String name, value;
	public XmlLeaf(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "<" + name + ">" + value + "</" + name + ">";
	}
}
