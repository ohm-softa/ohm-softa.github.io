package reflection.games;

import reflection.TextBasedGame;

import java.io.*;
import java.util.Random;

public class Addition implements TextBasedGame {
	@Override
	public void run(InputStream in, PrintStream out) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		out.println("How many rounds would you like to play?");
		int rounds = Integer.parseInt(br.readLine());

		Random rand = new Random();
		while (rounds-- > 0) {
			int a = Math.abs(rand.nextInt());
			int b = Math.abs(rand.nextInt());

			out.printf("  %12d\n+ %12d\n= ", a, b);
			int res = Integer.parseInt(br.readLine());

			if (res != a + b) {
				out.println("Not quite, it is " + (a + b));
				break;
			}

			out.println("Good job!");
		}
	}
}
