package designpattern.command;

import javakara.JavaKaraProgram;

public class MoveCommand implements Command {
	private JavaKaraProgram.JavaKara kara;
	public MoveCommand(JavaKaraProgram.JavaKara kara) {
		this.kara = kara;
	}

	@Override
	public void execute() {
		kara.move();
	}

	@Override
	public void undo() {
		// turn back, move
		new TurnCommand(kara, 2).execute();
		kara.move();

		// turn to original direction
		new TurnCommand(kara, 2).execute();
	}
}
