package com.start.helm;

import com.gargoylesoftware.htmlunit.WebClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.util.List;

import static com.start.helm.util.TestUtil.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelmChartTests {

	WebClient webClient;

	MockMvc mvc;

	@LocalServerPort
	int port;

	@Autowired
	WebApplicationContext ctx;

	@BeforeEach
	void before() {
		this.webClient = MockMvcWebClientBuilder.webAppContextSetup(ctx).build();
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx).alwaysDo(MockMvcResultHandlers.print()).build();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(HelmChartTests.class);

	@Test
	@SneakyThrows
	public void testHelmLint() {
		GenericContainer<?> c = new GenericContainer<>("alpine/helm:3.11.1")
			.withCopyToContainer(MountableFile.forClasspathResource("helm", 0777), "/apps")
			.withLogConsumer(new Slf4jLogConsumer(LOGGER))
			.withCommand("lint");
		c.start();

		waitForCondition(() -> !c.isRunning(), 10);
		Assertions.assertTrue(c.getLogs().contains("0 chart(s) failed"));

	}

	@Test
	@SneakyThrows
	public void helmUnittestPostgres() {
		// generate helm chart
		File chartDirectory = downloadStarter(this.mvc, "test-postgres-chart", "1.0.0", List.of("postgresql"));
		// copy helm unittest files to helmchart/tests directory
		addHelmUnittestFiles(chartDirectory, "postgres", List.of("deployment_postgres_test.yaml",
				"service_postgres_test.yaml", "configmap_postgres_test.yaml", "secrets_postgres_test.yaml"));
		addHelmUnittestValues(chartDirectory);

		GenericContainer<?> c = new GenericContainer<>("helmunittest/helm-unittest:3.11.1-0.3.0")
			.withCopyToContainer(MountableFile.forHostPath(chartDirectory.toPath(), 0777), "/apps")
			.withLogConsumer(new Slf4jLogConsumer(LOGGER))
			.withCommand("-o /apps/test-output.xml -t junit .".split(" "));
		c.start();
		waitForCondition(() -> !c.isRunning(), 10);
		Assertions.assertFalse(c.getLogs().contains("exited with error"));
	}

}
