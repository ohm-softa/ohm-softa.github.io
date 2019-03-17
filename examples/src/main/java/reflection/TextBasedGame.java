package reflection;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public interface TextBasedGame {
	void run(InputStream in, PrintStream out) throws IOException;
}
