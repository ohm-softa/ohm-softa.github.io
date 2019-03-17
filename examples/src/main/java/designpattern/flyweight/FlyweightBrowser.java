package designpattern.flyweight;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class FlyweightBrowser {
	public static void main(String[] args) throws IOException, URISyntaxException {
		List<Img> items = new LinkedList<>();

		FlyweightFactory fwf = new FlyweightFactory();

		String[] paths = {"artist1.bmp", "artist2.bmp", "artist3.bmp"};

		System.out.println("Lade Bilder...");
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			// retrieve flyweight from factory
			Flyweight f = fwf.getFlyweight(paths[i % paths.length]);

			// here we create the extrinsic state with a link to its intrinsic state
			items.add(new Img("Item " + i, f));
		}

		System.out.println(String.format("Zeit: %.3fs", (System.currentTimeMillis() - now) / 1000.));

		// Bilder ausgeben
		int n = 5;
		for (Img it : items) {
			it.describe(System.out);
			if (--n == 0)
				break;
		}
	}
}
