package com.start.helm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.start.helm.domain.ChartCountTracker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelmStartAppTests {

	@Autowired
	ResourceLoader resourceLoader;

	WebClient webClient;

	MockMvc mvc;

	@LocalServerPort
	int port;

	@Autowired
	WebApplicationContext ctx;

	@Value("${helm-start.data-directory:helm-start-data}")
	private String dataDirectory;

	@Autowired
	ObjectMapper om;

	@BeforeEach
	void before() {
		this.webClient = MockMvcWebClientBuilder.webAppContextSetup(ctx).build();
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx).alwaysDo(MockMvcResultHandlers.print()).build();
	}

	@Test
	public void testIndex() throws Exception {
		int i = new Random().nextInt();
		var model = new ChartCountTracker.ChartCount(i);
		Files.write(Paths.get(dataDirectory, "chart-count.json"), om.writeValueAsBytes(model));

		HtmlPage page = webClient.getPage("http://localhost:" + port + "/");
		Assertions.assertTrue(page.getWebResponse().getContentAsString().contains("hello world"));
		Assertions.assertTrue(
				page.getWebResponse().getContentAsString().contains("Helm Charts generated: <span>" + i + "</span>"));

	}

	@Test
	public void testUploadRabbit() throws Exception {

		String pom = resourceLoader.getResource("classpath:pom-with-rabbit.xml")
			.getContentAsString(StandardCharsets.UTF_8);

		// send file with mockmvc
		this.mvc.perform(multipart("/upload-file").file("file", pom.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void testUploadPostgres() throws Exception {

		String pom = resourceLoader.getResource("classpath:pom-with-postgres.xml")
			.getContentAsString(StandardCharsets.UTF_8);

		// send file with mockmvc
		this.mvc.perform(multipart("/upload-file").file("file", pom.getBytes())).andExpect(status().isOk());
	}

}
