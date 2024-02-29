package com.start.helm.initializr.spring;

import com.gargoylesoftware.htmlunit.WebClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuarkusInitializrProxyTest {

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
	void getStarter() {
		String url = "/quarkus/api/download";
		String body = """
				{	"buildTool":"MAVEN",\s
				    "groupId":"org.acme",\s
					"artifactId":"code-with-quarkus21",\s
					"version":"1.0.0-SNAPSHOT",\s
					"className":"org.acme.ExampleResource",\s
					"path":"/hello",\s
				    "noCode": "true",
				    "noExamples": "true",
				    "javaVersion": 17,
					"streamKey":"io.quarkus.platform:3.8",
					"extensions":[
						"io.quarkus:quarkus-resteasy-reactive-jackson",\s
						"io.quarkus:quarkus-jdbc-postgresql",\s
						"io.quarkus:quarkus-smallrye-reactive-messaging-rabbitmq",\s
						"io.quarkus:quarkus-resteasy-reactive-jackson"
					]
				}
				""";

		this.mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/octet-stream"))
			.andExpect(header().string("Content-Disposition", "attachment; filename=code-with-quarkus21.zip"))
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

				Assertions
					.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm")));
				Assertions.assertTrue(Files
					.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm", "Chart.yaml")));
				Assertions.assertTrue(Files
					.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm", "values.yaml")));

				Assertions.assertTrue(Files
					.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm", "templates")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "deployment.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "ingress.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "service.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "configmap.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "secrets.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "hpa.yaml")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "_helpers.tpl")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "NOTES.txt")));
				Assertions.assertTrue(Files.exists(Paths.get(extracted.getAbsolutePath(), "code-with-quarkus21", "helm",
						"templates", "serviceaccount.yaml")));

			});
	}

}
