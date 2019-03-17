package designpattern.flyweight;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

class NaiveImg {
	final Image image;
	final String caption;

	NaiveImg(String caption, String path) throws IOException, URISyntaxException {
		this.caption = caption;

		// get resource file uri
		File file = new File(getClass().getClassLoader()
				.getResource(path).toURI());

		// load image
		this.image = ImageIO.read(file);
	}

	void describe(PrintStream ps) {
		ps.println(String.format("%s: %d x %d",
				caption,
				image.getHeight(null),
				image.getWidth(null)
		));
	}
}
