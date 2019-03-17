package generics;

public class SimpleStringIntMap extends SimpleMapImpl {

	public void put(String key, Integer value) {
		super.put(key, value);
	}

	public Integer get(String key) {
		Object val = super.get(key);
		if (val == null)
			return null;
		else
			return (Integer) val;
	}

	public static void main(String[] args) {
		SimpleStringIntMap map = new SimpleStringIntMap();
		map.put("Hans", 14235);
		System.out.println("Hans: " + map.get("Hans"));
		Integer hans = map.get("Hans");

		map.put("Peter", "Willi");  // debugger!
		Integer peter = map.get("Peter");
		System.out.println("Peter: " + map.get("Peter"));
	}
}
