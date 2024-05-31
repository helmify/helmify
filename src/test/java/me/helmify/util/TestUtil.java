package me.helmify.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.dependencies.FrameworkVendor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class QuarkusRequest {

		String buildTool;

		String groupId;

		String artifactId;

		String version;

		String className;

		String path;

		boolean noCode;

		boolean noExamples;

		int javaVersion;

		String streamKey;

		List<String> extensions;

	}

	@SneakyThrows
	public static File downloadStarter(MockMvc mvc, String name, String version, List<String> dependencies,
			String chartFlavor, FrameworkVendor vendor) {

		MvcResult result = null;

		switch (vendor) {
			case Spring -> {

				String bootVersion = "3.2.2";
				String javaVersion = "21";
				String groupId = "com.example";
				String deps = String.join(",", dependencies);
				String url = String.format(
						"/spring/starter.zip?chartFlavor=%s&bootVersion=%s&javaVersion=%s&groupId=%s&name=%s&description=%s&artifactId=%s&language=java&packaging=jar&packageName=%s&type=gradle-project&version=%s&dependencies=%s",
						chartFlavor, bootVersion, javaVersion, groupId, name, name, name, groupId, version, deps);

				result = mvc.perform(get(url)).andExpect(status().isOk()).andReturn();

			}
			case Quarkus -> {
				String url = "/quarkus/api/download";
				QuarkusRequest request = new QuarkusRequest();
				request.setExtensions(new ArrayList<>());
				request.setVersion("1.0.0");
				request.setBuildTool("MAVEN");
				request.setArtifactId(name);
				request.setGroupId("com.example");
				request.setJavaVersion(17);
				dependencies.stream().map(d -> "io.quarkus:" + d).forEach(request.getExtensions()::add);

				String s = new ObjectMapper().writeValueAsString(request);

				result = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(s)).andReturn();

			}
		}

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

		return FrameworkVendor.Spring.equals(vendor) ? Paths.get(parent.getAbsolutePath(), "helm").toFile()
				: Paths.get(parent.getAbsolutePath(), name, "helm").toFile();
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
