package designpattern.strategy;

import designpattern.strategy.Strategy;
import javakara.JavaKaraProgram;

public class SnakeStrategy implements Strategy {
	private JavaKaraProgram.JavaKara kara;
	public SnakeStrategy(JavaKaraProgram.JavaKara kara) {
		this.kara = kara;
	}

	@Override
	public void explore() {
		// explore by snake-like trail
		kara.putLeaf();

		int n = 1;
		while (!kara.treeFront()) {
			for (int i = 0; i < n; i++) {
				kara.move();
				kara.putLeaf();
				if (i < n - 1 && kara.treeFront())
					return;
			}
			kara.turnLeft();

			for (int i = 0; i < n; i++) {
				kara.move();
				kara.putLeaf();

				if (i < n - 1 && kara.treeFront())
					return;
			}
			kara.turnLeft();

			n++;
		}
	}
}
