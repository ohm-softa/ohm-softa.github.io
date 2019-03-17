package designpattern.command;

import javakara.JavaKaraProgram;

public class DropCommand implements Command {
	private JavaKaraProgram.JavaKara kara;

	public DropCommand(JavaKaraProgram.JavaKara kara) {
		this.kara = kara;
	}

	@Override
	public void execute() {
		kara.putLeaf();
	}

	@Override
	public void undo() {
		kara.removeLeaf();
	}
}
