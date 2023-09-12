package com.start.helm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

	@BeforeEach
	void before() {
		this.webClient = MockMvcWebClientBuilder.webAppContextSetup(ctx).build();
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx)
				.alwaysDo(MockMvcResultHandlers.print()).build();
	}

	@Test
	public void testIndex() throws IOException {
		HtmlPage page = webClient.getPage("http://localhost:" + port + "/");
		Assertions.assertTrue(page.getWebResponse().getContentAsString().contains("hello world"));
	}

	@Test
	public void testUploadRabbit() throws Exception {
		MockMvc build = MockMvcBuilders.webAppContextSetup(ctx)
				.alwaysDo(MockMvcResultHandlers.print())
				.build();

		String pom =
				resourceLoader.getResource("classpath:pom-with-rabbit.xml").getContentAsString(StandardCharsets.UTF_8);

		// send file with mockmvc
		build.perform(multipart("/upload-file").file("file", pom.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void testUploadPostgres() throws Exception {
		MockMvc build = MockMvcBuilders.webAppContextSetup(ctx)
				.alwaysDo(MockMvcResultHandlers.print())
				.build();

		String pom =
				resourceLoader.getResource("classpath:pom-with-postgres.xml").getContentAsString(StandardCharsets.UTF_8);

		// send file with mockmvc
		build.perform(multipart("/upload-file").file("file", pom.getBytes())).andExpect(status().isOk());
	}
}
