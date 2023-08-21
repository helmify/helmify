package com.start.helm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application Entry Point.
 */
@SpringBootApplication
public class HelmStartApp {

  public static void main(String[] args) {

    ConfigurableApplicationContext run = SpringApplication.run(HelmStartApp.class, args);
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
