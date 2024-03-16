package me.helmify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application Entry Point.
 */
@SpringBootApplication
public class HelmifyApp {

	public static void main(String[] args) {

		ConfigurableApplicationContext run = SpringApplication.run(HelmifyApp.class, args);
		Integer port = run.getEnvironment().getProperty("server.port", Integer.class, 8080);
		print(port);
	}

	private static void print(int port) {
		System.out.println("=============================================================");
		System.out.println();
		System.out.println("\t start at http://localhost:" + port + "/");
		System.out.println();
		System.out.println("=============================================================");
	}

}
