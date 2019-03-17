package designpattern.flyweight;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

class Flyweight {
	// intrinsic state
	private final Image image;

	Flyweight(String path) throws URISyntaxException, IOException {
		// get resource file uri
		File file = new File(getClass().getClassLoader()
				.getResource(path).toURI());

		// load image (the intrinsic state)
		this.image = ImageIO.read(file);
	}

	void describe(PrintStream ps, Img es) {
		ps.println(String.format("%s: %d x %d",
				es.caption,
				image.getHeight(null),
				image.getWidth(null)
		));
	}
}
