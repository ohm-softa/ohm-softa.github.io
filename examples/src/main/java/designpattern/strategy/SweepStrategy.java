package designpattern.strategy;

import javakara.JavaKaraProgram;

public class SweepStrategy implements Strategy {
	private JavaKaraProgram.JavaKara kara;
	public SweepStrategy(JavaKaraProgram.JavaKara kara) {
		this.kara = kara;
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


	@Override
	public void explore() {
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
