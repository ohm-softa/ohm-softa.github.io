package generics;

import java.util.ArrayList;
import java.util.List;

public class Arrays {
	public static void main(String[] args) {
		Number n;
		Integer i = 5;

		n = i;  // Integer extends Number

		Number[] na;
		Integer[] ia = {1, 2, 3, 4};
		na = ia;


//		ArrayList ns;
//		ArrayList<Integer> is = new ArrayList<>(1);
//		is.add(1);
//		ArrayList<String> nns;
//		ns = is;
//		nns = ns;
//		nns = (ArrayList<String>) is;
//		System.out.println(nns.get(0));

		List<Integer> li;
		ArrayList<Integer> al = new ArrayList<>();
		li = al;
	}
}
