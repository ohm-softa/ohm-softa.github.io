package designpattern.factory;

public class UsageBasic {
	public static void main(String[] args) {
		JsonComposite root = new JsonComposite("root");
		root.add(new JsonLeaf("key", "value"));

		Composite nested = new JsonComposite("nested");
		nested.add(new JsonLeaf("key", "value"));
		root.add(nested);

		System.out.println(root);
	}
}
