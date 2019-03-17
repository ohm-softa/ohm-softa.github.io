package designpattern.proxyadapter;

public interface MealProvider extends Iterable<Meal> {
	void setDate(String date);
}
