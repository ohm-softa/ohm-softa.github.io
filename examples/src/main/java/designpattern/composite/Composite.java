package designpattern.composite;

public class Composite extends Component {
	private Component[] contents;
	@Override
	public int count() {
		int sum = 0;
		for (Component c : contents)
			sum += c.count();
		return sum;
	}
}
