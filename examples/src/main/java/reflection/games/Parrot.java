package reflection.games;

import reflection.TextBasedGame;

import java.io.*;

public class Parrot implements TextBasedGame {
	@Override
	public void run(InputStream in, PrintStream out) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		out.println("Welcome to parrot. Please say something");

		String line;
		while ((line = reader.readLine()) != null && line.length() > 0) {
			out.println("You said: " + line);
		}
		out.println("Bye!");
	}
}
