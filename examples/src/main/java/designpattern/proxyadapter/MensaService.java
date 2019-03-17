package designpattern.proxyadapter;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MensaService {
	final static Logger logger = LoggerFactory.getLogger(MensaService.class);

	public interface OpenMensaApi {
		@GET("canteens/229/days/{date}/meals")
		Call<List<Meal>> listMeals(@Path("date") String date);
	}

	private OpenMensaApi api;

	public MensaService() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://openmensa.org/api/v2/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		api = retrofit.create(OpenMensaApi.class);
	}

	public List<Meal> getMeals(String date) throws IOException {
		Call<List<Meal>> call = api.listMeals(date);

		// retrieve meals
		logger.info("Retrieving all meals for " + date);
		Response<List<Meal>> resp = call.execute();

		return resp.body();
	}

	public static void main(String... args) throws IOException {
		Gson gson = new Gson();

		MensaService ms = new MensaService();

		MensaService proxy = new MensaService() {
			Map<String, List<Meal>> cache = new HashMap<>();
			public List<Meal> getMeals(String date) throws IOException {
				if (cache.containsKey(date)) {
					logger.info("Cache hit!");
					return cache.get(date);
				}

				List<Meal> meals = super.getMeals(date);
				cache.put(date, meals);
				return meals;
			}
		};

		final int n = 100;
		for (int i = 0; i < n; i++)
			System.out.println(proxy.getMeals("20171206"));

	}
}
