package designpattern.strategy;

import javakara.JavaKaraProgram;

import java.io.IOException;

public class StrategyExampleBad extends JavaKaraProgram {
	public static void main(String[] args) throws IOException, InterruptedException {
		StrategyExampleBad program = new StrategyExampleBad();
		program.run("src/main/resources/world2.world");

		int c;
		while ((c = System.in.read()) != -1) {
			program.planB();
		}
	}

	public void planA() throws InterruptedException {
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

	private boolean hasNext(boolean left) {
		if (left)
			return !kara.treeLeft();
		else
			return !kara.treeRight();
	}

	private void gotoNext(boolean left) {
		if (left) {
			kara.turnLeft();
			kara.move();
			kara.turnLeft();
		} else {
			kara.turnRight();
			kara.move();
			kara.turnRight();
		}
	}

	public void planB() {
		// move to side, track to top, do s-trails
		while (!kara.treeFront())
			kara.move();
		kara.turnLeft();
		while (!kara.treeFront())
			kara.move();
		kara.turnLeft();

		// first sweep
		kara.putLeaf();
		while (!kara.treeFront()) {
			kara.move();
			kara.putLeaf();
		}

		boolean left = true;

		// kara faces dowmn
		while (hasNext(left)) {
			gotoNext(left);
			kara.putLeaf();
			while (!kara.treeFront()) {
				kara.move();
				kara.putLeaf();
			}
			left = !left;
		}
	}
}
