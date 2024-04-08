package me.helmify.domain.helm;

import lombok.SneakyThrows;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import me.helmify.util.TestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class HelmChartTests extends HelmLintBaseTest {

	protected MockMvc mvc;

	@LocalServerPort
	protected int port;

	@Autowired
	protected WebApplicationContext ctx;

	private static List<String> toDelete = new ArrayList<>();

	@BeforeEach
	void before() {
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx).alwaysDo(MockMvcResultHandlers.print()).build();
	}

	@AfterAll
	static void afterAll() {
		for (String path : toDelete) {
			try {
				LOGGER.info("Deleting path: " + path);
				Path toDelete = Paths.get(path);
				Files.walk(toDelete).map(Path::toFile).forEach(File::delete);
			}
			catch (Exception e) {
				LOGGER.error("Failed to delete path: " + path, e);
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(HelmChartTests.class);

	public record HelmUnittestContext(String chartName, String chartVersion, List<String> dependencies,
			String unittestSourceDirectory, List<String> unittestFiles, String chartFlavor, FrameworkVendor vendor) {
	}

	@SneakyThrows
	public void lintAndTestChart(HelmUnittestContext context) {
		// generate chart
		File chartDirectory = TestUtil.downloadStarter(this.mvc, context.chartName, context.chartVersion,
				context.dependencies, context.chartFlavor, context.vendor);
		toDelete.add(chartDirectory.getAbsolutePath());

		System.out.println("chartDirectory = " + chartDirectory);

		String containerLog = lintHelmChart(chartDirectory.getAbsolutePath());
		Assertions.assertTrue(containerLog.contains("0 chart(s) failed"));

		boolean shouldTest = !context.unittestFiles.isEmpty();

		if (shouldTest) {

			// copy helm unittest files to helmchart/tests directory
			TestUtil.addHelmUnittestFiles(chartDirectory, context.unittestSourceDirectory, context.unittestFiles);
			TestUtil.addHelmUnittestValues(chartDirectory);

			// helm-unittest chart
			GenericContainer<?> unittestContainer = new GenericContainer<>("helmunittest/helm-unittest:latest")
				.withCopyToContainer(MountableFile.forHostPath(chartDirectory.toPath(), 0777), "/apps")
				.withLogConsumer(new Slf4jLogConsumer(LOGGER))
				.withCommand("-o /apps/test-output.xml -t junit .".split(" "));
			unittestContainer.start();
			TestUtil.waitForCondition(() -> !unittestContainer.isRunning(), 10);
			Assertions.assertFalse(unittestContainer.getLogs().contains("exited with error"));
		}
	}

}
