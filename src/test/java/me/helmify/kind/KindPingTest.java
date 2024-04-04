package me.helmify.kind;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
public class KindPingTest {

	@Test
	public void testCluster() throws Exception {

		// only execute when explicitly called
		String property = System.getProperty("test");
		if (property == null || property.isEmpty() || !"KindPingTest".equals(property)) {
			return;
		}

		String url = System.getProperty("url");
		String expected = System.getProperty("expected");

		int tries = 0;
		int max = 3;

		boolean checkResponse = false;

		while (tries < max) {
			checkResponse = checkResponse(url, expected);
			if (checkResponse) {
				break;
			}
			Thread.sleep(3000);
			tries++;
		}

		Assertions.assertTrue(checkResponse, "Expected response not found from: " + expected);

	}

	private boolean checkResponse(String url, String expected) throws Exception {
		// response from url should contain expected string. response from url is a
		// hashmap
		RestClient client = RestClient.builder().build();
		Map<String, Boolean> body = client.get().uri(url).retrieve().body(mapType);
		return body != null && body.containsKey(expected) && body.get(expected);
	}

	ParameterizedTypeReference<Map<String, Boolean>> mapType = new ParameterizedTypeReference<>() {
	};

}
