package reflection;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class TextGameLoader {
	public static void main(String... args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		// TODO load classes from jar file
		URL url = new URL("jar:file:/Users/riko493/Downloads/games.jar!/");
		URLClassLoader cl = URLClassLoader.newInstance(new URL[] {url});

		// you can play "Addition" or "Parrot"
		final String choice = "Parrot";

		TextBasedGame g = (TextBasedGame) cl.loadClass("reflection.games." + choice)
				.newInstance();
		g.run(System.in, System.out);
	}
}
