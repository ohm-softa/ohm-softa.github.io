package designpattern.command;

import javakara.JavaKaraProgram;

public class TurnCommand implements Command {
	private int n;
	private JavaKaraProgram.JavaKara kara;
	public TurnCommand(JavaKaraProgram.JavaKara kara, int n) {
		this.kara = kara;
		this.n = n;
	}

	@Override
	public void execute() {
		int i = n;

		if (i > 0) {
			while (i-- > 0)
				kara.turnRight();
		} else {
			while (i++ < 0)
				kara.turnLeft();
		}
	}

	@Override
	public void undo() {
		int i = n;
		if (i > 0) {
			while (i-- > 0)
				kara.turnLeft();
		} else {
			while (i++ < 0)
				kara.turnRight();
		}
	}
}
