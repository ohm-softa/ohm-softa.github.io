package designpattern.iterator;

public class Usage {
	public static void main(String[] args) {
		int[] array = new int [] {1, 3, 3, 7};

		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}

		for (int v : array) {
			System.out.println(v);
		}

		SimpleList<Integer> list = new SimpleList<>(3, 1, 3, 3, 7);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
}
