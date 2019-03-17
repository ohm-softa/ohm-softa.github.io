package diamond;

public class Klass implements Top {
	public void method() {
		Top.super.method();
		System.out.println("Klass.method()");
	}
	public static void main(String[] args) {
		Klass k = new Klass();
		k.method();
	}
}
