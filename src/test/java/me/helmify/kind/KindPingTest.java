package me.helmify.kind;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class KindPingTest {

	@Test
	public void testCluster() {

		// only execute when explicitly called
		String property = System.getProperty("test");
		if (property == null || property.isEmpty() || !"KindPingTest".equals(property)) {
			return;
		}

		String url = System.getProperty("url");
		String expected = System.getProperty("expected");

		List<String> expectations = expected.contains(",") ? Arrays.asList(expected.split(",")) : List.of(expected);
		Map<String, Boolean> fulfilledExpectations = expectations.stream()
			.collect(Collectors.toMap(e -> e, e -> false));

		fulfilledExpectations.keySet().forEach(exp -> {
			int tries = 0;
			int max = 10;
			while (tries < max) {
				String endpoint = url + "/" + exp;
				boolean response = checkResponse(endpoint, exp);
				log.info("Got Response from endpoint {} : {}", endpoint, response);
				if (response) {
					fulfilledExpectations.put(exp, true);
					break;
				}
				sleep();
				tries++;
			}
		});

		log.info("Current Expectations: {}", fulfilledExpectations);

		fulfilledExpectations.keySet().forEach(k -> {
			Assertions.assertTrue(fulfilledExpectations.get(k), "Expected response not found from: " + k);
		});

	}

	@SneakyThrows
	private static void sleep() {
		Thread.sleep(5000);
	}

	private boolean checkResponse(String url, String expected) {
		// response from url should contain expected string. response from url is a
		// hashmap
		RestClient client = RestClient.builder().build();
		Map<String, Boolean> body = client.get().uri(url).retrieve().body(mapType);
		log.info("Received response from {} for key {} : {}", url, expected, body);
		return body != null && body.containsKey(expected) && body.get(expected);
	}

	ParameterizedTypeReference<Map<String, Boolean>> mapType = new ParameterizedTypeReference<>() {
	};

}
