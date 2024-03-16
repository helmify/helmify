package me.helmify.initializr.spring;

import com.gargoylesoftware.htmlunit.WebClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringInitializrProxyTest {

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

	@Test
	@SneakyThrows
	void getCapabilities() {
		this.mvc.perform(get("/spring"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/vnd.initializr.v2.2+json"))
			.andExpect(jsonPath("$._links").isNotEmpty())
			.andExpect(jsonPath("$.dependencies").isNotEmpty())
			.andExpect(jsonPath("$.type").isNotEmpty())
			.andExpect(jsonPath("$.packaging").isNotEmpty())
			.andExpect(jsonPath("$.javaVersion").isNotEmpty())
			.andExpect(jsonPath("$.language").isNotEmpty())
			.andExpect(jsonPath("$.bootVersion").isNotEmpty())
			.andExpect(jsonPath("$.groupId").isNotEmpty())
			.andExpect(jsonPath("$.artifactId").isNotEmpty())
			.andExpect(jsonPath("$.version").isNotEmpty())
			.andExpect(jsonPath("$.name").isNotEmpty())
			.andExpect(jsonPath("$.description").isNotEmpty())
			.andExpect(jsonPath("$.packageName").isNotEmpty());

	}

	@Test
	@SneakyThrows
	void getStarter() {
		String url = "/spring/starter.zip?bootVersion=3.1.4&javaVersion=17&groupId=com.example&name=demo10&description=demo10&artifactId=demo10&language=java&packaging=jar&packageName=com.example.demo&type=gradle-project&version=0.0.1-SNAPSHOT&dependencies=amqp,postgresql,web";
		this.mvc.perform(get(url))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/octet-stream"))
			.andExpect(header().string("Content-Disposition", "attachment; filename=starter.zip"))
			.andDo(result -> {
				byte[] content = result.getResponse().getContentAsByteArray();
				assertNotNull(content);
				assertTrue(content.length > 0);

				File parent = Paths.get(System.getProperty("java.io.tmpdir"), "test-" + System.currentTimeMillis())
					.toFile();
				parent.mkdirs();

				File file = Paths.get(parent.getAbsolutePath(), "starter.zip").toFile();
				Files.write(file.toPath(), content);
				assertTrue(file.exists());

				File extracted = Paths.get(file.getParentFile().getAbsolutePath(), "extracted").toFile();
				extracted.mkdirs();

				ZipUtil.unpack(file, extracted);

				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "helm")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "Chart.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "values.yaml")));

				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates")));
				Assertions.assertTrue(
						Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "deployment.yaml")));
				Assertions.assertTrue(
						Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "ingress.yaml")));
				Assertions.assertTrue(
						Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "service.yaml")));
				Assertions.assertTrue(
						Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "configmap.yaml")));
				Assertions.assertTrue(
						Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "secrets.yaml")));
				Assertions
					.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "hpa.yaml")));
				Assertions.assertTrue(
						Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "_helpers.tpl")));
				Assertions
					.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "NOTES.txt")));
				Assertions.assertTrue(Files
					.exists(Paths.get(extracted.getAbsolutePath(), "helm", "templates", "serviceaccount.yaml")));

			});
	}

}
