package designpattern.factory;

public class Usage {
	public static void main(String[] args) {
		CompositeFactory f = new JsonFactory();
		// CompositeFactory f = new XmlFactory();

		Composite root = f.createComposite("root");
		root.add(f.createLeaf("key", "value"));

		Composite nested = f.createComposite("nested");
		nested.add(f.createLeaf("key", "value"));

		root.add(nested);

		System.out.println(root);
	}
}
