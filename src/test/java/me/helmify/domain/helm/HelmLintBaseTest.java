package me.helmify.domain.helm;

import me.helmify.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.nio.file.Paths;

public class HelmLintBaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelmLintBaseTest.class);

	public String lintHelmChart(String helmDirectory) {
		StringBuffer buffer = new StringBuffer();
		GenericContainer<?> container = new GenericContainer<>("helmify/helm-linter:latest");

		for (File f : Paths.get(helmDirectory).toFile().listFiles()) {
			if (f.isFile()) {
				container = container.withCopyToContainer(MountableFile.forHostPath(f.getAbsolutePath()),
						"/chart/" + f.getName());
			}
		}

		container

			.withLogConsumer(new Slf4jLogConsumer(LOGGER))
			.withLogConsumer(outputFrame -> buffer.append(outputFrame.getUtf8String()))
			.start();

		GenericContainer<?> finalContainer = container;
		TestUtil.waitForCondition(() -> !finalContainer.isRunning(), 30);

		return buffer.toString();
	}

}
