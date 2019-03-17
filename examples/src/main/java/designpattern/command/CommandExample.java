package designpattern.command;

import javakara.JavaKaraProgram;
import java.io.IOException;
import java.util.Stack;

public class CommandExample extends JavaKaraProgram {
	public static void main(String[] args) throws IOException, InterruptedException {
		CommandExample program = new CommandExample();
		program.run("src/main/resources/world1.world");

		Stack<Command> history = new Stack<>();

		int c;
		while ((c = System.in.read()) != -1) {
			if (c == 10)
				continue;  // enter
			else if ((char) c == 'e')
				break;

			Command cmd = new IdleCommand();
			switch ((char) c) {
				case 'm': cmd = new MoveCommand(program.kara); break;
				case 'l': cmd = new TurnCommand(program.kara, -1); break;
				case 'r': cmd = new TurnCommand(program.kara, 1); break;
				case 't': cmd = new PickupCommand(program.kara); break;
				case 'd': cmd = new PickupCommand(program.kara); break;
				case 's': history.clear(); break;
			}

			try {
				cmd.execute();
				history.push(cmd);
			} catch (RuntimeException e) {
				System.out.println(e);
				System.out.println("Tracking back to last saved state");
				// go back to beginning, restart
				while (history.size() > 0)
					history.pop().undo();
			}
		}

		System.exit(0);
	}
}
