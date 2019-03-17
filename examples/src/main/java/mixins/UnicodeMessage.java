package mixins;

import java.nio.charset.Charset;

public class UnicodeMessage extends Message {
	public byte[] utf8() {
		return text().getBytes(Charset.forName("UTF-8"));
	}
}
