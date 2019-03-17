package designpattern.command;

import javakara.JavaKaraProgram;

public class PickupCommand implements Command {
	private JavaKaraProgram.JavaKara kara;

	public PickupCommand(JavaKaraProgram.JavaKara kara) {
		this.kara = kara;
	}

	@Override
	public void execute() {
		kara.removeLeaf();
	}

	@Override
	public void undo() {
		kara.putLeaf();
	}
}
