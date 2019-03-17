package library;

public interface TollesInterface {
	void toWas();

	static TollesInterface create() {
		return new TollesInterfaceImpl();
	}
}
