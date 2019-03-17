package designpattern.flyweight;

import java.io.PrintStream;

class Img {
	final String caption;
	final Flyweight flyweight;

	Img(String caption, Flyweight flyweight) {
		this.caption = caption;
		this.flyweight = flyweight;
	}

	void describe(PrintStream ps) {
		// inject extrinsic state
		flyweight.describe(ps, this);
	}
}
