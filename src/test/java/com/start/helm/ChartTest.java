package com.start.helm;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

import java.util.function.Supplier;

@SpringBootTest

public class ChartTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartTest.class);

	@Test
	@SneakyThrows
	public void testHelmLint() {
		GenericContainer<?> c = new GenericContainer<>("alpine/helm:3.11.1")
			.withCopyToContainer(MountableFile.forClasspathResource("helm", 0777), "/apps")
			.withLogConsumer(new Slf4jLogConsumer(LOGGER))
			.withCommand("lint");
		c.start();

		Util.waitForCondition(() -> !c.isRunning(), 10);
		Assertions.assertTrue(c.getLogs().contains("0 chart(s) failed"));

	}

	@Test
	@SneakyThrows
	public void testHelmUnittest() {
		GenericContainer<?> c = new GenericContainer<>("helmunittest/helm-unittest:3.11.1-0.3.0")
			.withCopyToContainer(MountableFile.forClasspathResource("helm", 0777), "/apps")
			.withLogConsumer(new Slf4jLogConsumer(LOGGER))
			.withCommand("-o /apps/test-output.xml -t junit .".split(" "));
		c.start();
		Util.waitForCondition(() -> !c.isRunning(), 10);
		Assertions.assertFalse(c.getLogs().contains("exited with error"));
	}

	class Util {

		@SneakyThrows
		public static boolean waitForCondition(Supplier<Boolean> condition, int waitSeconds) {
			int sleepMillis = 1000;
			int attempts = 0;
			do {
				Boolean result = condition.get();
				if (result) {
					return true;
				}
				attempts++;
				sleep(sleepMillis);
			}
			while (attempts < waitSeconds);
			return false;
		}

		@SneakyThrows
		private static void sleep(int sleepMillis) {
			Thread.sleep(sleepMillis);
		}

	}

}
