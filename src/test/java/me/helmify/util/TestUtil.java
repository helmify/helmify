package me.helmify.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class TestUtil {

	@SneakyThrows
	public static String inputStreamToString(InputStream is) {
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] bytes = IOUtils.toByteArray(bis);
		return new String(bytes);
	}

	/**
	 * Adds specified files from classpath to a helm chart directory
	 */
	@SneakyThrows
	public static void addHelmUnittestFiles(File chartDirectory, String sourceDir, List<String> files) {
		File helmUnittestDirectory = Paths.get(chartDirectory.getAbsolutePath(), "tests").toFile();
		helmUnittestDirectory.mkdirs();

		for (String helmUnittestFile : files) {
			log.info("Copying helm unittest file {} to {}", helmUnittestFile, helmUnittestDirectory.getAbsolutePath());
			// copy helm unittest files from classpath to chart directory
			Files.copy(
					TestUtil.class.getClassLoader()
						.getResourceAsStream("helm-unittests/" + sourceDir + "/" + helmUnittestFile),
					Paths.get(helmUnittestDirectory.getAbsolutePath(), helmUnittestFile));
		}
	}

	@SneakyThrows
	public static void addHelmUnittestValues(File chartDirectory) {
		File helmValuesFile = Paths.get(chartDirectory.getAbsolutePath(), "values.yaml").toFile();
		Files.copy(helmValuesFile.toPath(), Paths.get(chartDirectory.getAbsolutePath(), "tests", "values.yaml"));
	}

	/**
	 * Downloads a Spring Boot Project and unpacks it into a temporary directory. Returns
	 * a file reference to the unpacked starter zip's helm directory
	 */
	@SneakyThrows
	public static File downloadStarter(MockMvc mvc, String name, String version, List<String> dependencies) {
		String bootVersion = "3.2.2";
		String javaVersion = "21";
		String groupId = "com.example";
		String deps = String.join(",", dependencies);

		final String url = String.format(
				"/spring/starter.zip?bootVersion=%s&javaVersion=%s&groupId=%s&name=%s&description=%s&artifactId=%s&language=java&packaging=jar&packageName=%s&type=gradle-project&version=%s&dependencies=%s",
				bootVersion, javaVersion, groupId, name, name, name, groupId, version, deps);

		MvcResult result = mvc.perform(get(url)).andExpect(status().isOk()).andReturn();

		byte[] content = result.getResponse().getContentAsByteArray();
		assertNotNull(content);
		assertTrue(content.length > 0);

		File parent = Paths.get(System.getProperty("java.io.tmpdir"), "test-" + System.currentTimeMillis()).toFile();
		parent.mkdirs();

		File file = Paths.get(parent.getAbsolutePath(), "starter.zip").toFile();
		Files.write(file.toPath(), content);
		assertTrue(file.exists());

		ZipUtil.unpack(file, parent);

		log.info("Unpacked starter zip to {}", parent.getAbsolutePath());

		return Paths.get(parent.getAbsolutePath(), "helm").toFile();
	}

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
