package designpattern.proxyadapter;

import java.util.Iterator;

public class MealAdapter extends MensaService implements MealProvider {
	private String date;

	@Override
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public Iterator<Meal> iterator() {
		try {
			return super.getMeals(date).iterator();
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		MealAdapter ma = new MealAdapter();
		ma.setDate("20171206");
		for (Meal m : ma) {
			System.out.println(m);

		}
	}
}
