package designpattern.command;

public interface Command {
	void execute();
	default void undo() {
		throw new UnsupportedOperationException();
	}
}
