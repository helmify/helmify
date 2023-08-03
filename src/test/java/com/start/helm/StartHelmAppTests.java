package com.start.helm;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StartHelmAppTests {

	WebClient webClient;

	@LocalServerPort
	int port;

	@BeforeEach
	void before(WebApplicationContext ctx) {
		this.webClient = MockMvcWebClientBuilder.webAppContextSetup(ctx).build();
	}

	@Test
	public void testIndex () throws IOException {

		Page page = webClient.getPage("http://localhost:" + port + "/");
		Assertions.assertTrue(page.getWebResponse().getContentAsString().contains("hello world"));

	}

}
