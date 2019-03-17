package designpattern.command;

public class IdleCommand implements Command {
	@Override
	public void execute() {
		// nothing to do...
	}

	@Override
	public void undo() {
		// shooby do...
	}
}
