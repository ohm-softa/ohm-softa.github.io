package futures;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public class BetterWorkflow {
	public static void main(String[] args) throws InterruptedException, IOException {
		CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> "riko493:12345")
				.thenApplyAsync(creds -> {
					System.out.println("Authenticating with " + creds);
					return "secrettoken";
				})
				.thenApplyAsync(token -> {
					System.out.println("Retrieving status with token=" + token);
					return "in the mood for holidays";
				})
				.thenAccept(status -> System.out.println(status))
				.exceptionally(ex -> { System.out.println("Oops, something went wrong: " + ex); return null; });

		System.out.println("Ready to quit.");

		System.in.read();
	}
}
