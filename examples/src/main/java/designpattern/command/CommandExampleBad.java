package designpattern.command;

import javakara.JavaKaraProgram;
import java.io.IOException;

public class CommandExampleBad extends JavaKaraProgram {
	public static void main(String[] args) throws IOException, InterruptedException {
		CommandExampleBad program = new CommandExampleBad();
		program.run("src/main/resources/world1.world");

		int c;
		while ((c = System.in.read()) != -1) {
			if (c == 10)
				continue;  // enter
			else if ((char) c == 'e')
				break;

			try {
				switch ((char) c) {
					case 'm': program.kara.move(); break;
					case 'l': program.kara.turnLeft(); break;
					case 'r': program.kara.turnRight(); break;
					case 't': program.kara.removeLeaf(); break;
					case 'd': program.kara.putLeaf(); break;
				}
			} catch (RuntimeException e) {
				System.out.println(e);
				System.exit(0);
			}
		}

		System.exit(0);
	}
}
