package designpattern.flyweight;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

class NaiveBrowser {
	public static void main(String[] args) throws IOException, URISyntaxException {
		List<NaiveImg> items = new LinkedList<>();

		String[] paths = {"artist1.bmp", "artist2.bmp", "artist3.bmp"};

		System.out.println("Lade Galeriebilder...");
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			items.add(new NaiveImg("Item " + i, paths[i % paths.length]));
		}
		System.out.println(String.format("Zeit: %.3fs", (System.currentTimeMillis() - now) / 1000.));

		// Bilder ausgeben
		int n = 5;
		for (NaiveImg it : items) {
			it.describe(System.out);
			if (--n == 0)
				break;
		}
	}
}
