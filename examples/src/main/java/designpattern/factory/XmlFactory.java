package designpattern.factory;

public class XmlFactory implements CompositeFactory {
	@Override
	public Composite createComposite(String name) {
		return new XmlComposite(name);
	}

	@Override
	public Leaf createLeaf(String name, String value) {
		return new XmlLeaf(name, value);
	}
}
