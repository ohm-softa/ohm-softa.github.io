package designpattern.factory;

public interface CompositeFactory {
	Composite createComposite(String name);
	Leaf createLeaf(String name, String value);
}
