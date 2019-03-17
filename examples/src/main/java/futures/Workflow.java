package futures;

import java.util.concurrent.ExecutionException;

public class Workflow {
	static void displayStatus() throws ExecutionException, InterruptedException {
		Executor ex = new SimpleExecutor();
		final String user = "riko493";
		final String pass = "12345";  // spaceballs, anyone? :-)

		// log in...
		Future<String> f1 = ex.async(new Callable<String>() {
			public String call() {
				System.out.println("Authenticating with " + user + ":" + pass);
				return "secrettoken";
			}
		});

		final String token = f1.get();

		// retrieve user
		Future<String> f2 = ex.async(new Callable<String>() {
			public String call() {
				System.out.println("Retrieving user details with token " + token);
				return "lightly sleep deprived, should get haircut";
			}
		});

		final String details = f2.get();

		System.out.println("Welcome " + user + "! You look " + details);
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		displayStatus();
	}
}
