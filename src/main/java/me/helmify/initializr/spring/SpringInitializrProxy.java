package me.helmify.initializr.spring;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.initializr.InitializrSupport;
import me.helmify.util.DownloadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SpringInitializrProxy {

	private final InitializrSupport initializrSupport;

	@Value("${spring.initializr.host}")
	private String initializrHost;

	private String getInitializrHost() {
		return String.format("https://%s/", this.initializrHost);
	}

	@GetMapping(value = "/spring")
	public Object getCapabilities() {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.valueOf("application/vnd.initializr.v2.2+json")));
		HttpEntity<Object> entity = new HttpEntity<>(headers);

		return restTemplate.exchange(getInitializrHost(), HttpMethod.GET, entity, Object.class);
	}

	@GetMapping(value = "/spring/starter.zip")
	public ResponseEntity<?> getStarter(HttpServletRequest request) throws IOException {

		RestTemplate restTemplate = new RestTemplate();

		Map<String, List<String>> collected = request.getParameterMap()
			.keySet()
			.stream()
			.collect(Collectors.toMap(k -> k, k -> Arrays.asList(request.getParameterMap().get(k))));

		URI uri = UriComponentsBuilder.fromHttpUrl(getInitializrHost() + "starter.zip")
			.queryParams(new MultiValueMapAdapter<>(collected))
			.build()
			.toUri();

		ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(uri, byte[].class);

		if (forEntity.getStatusCode().is2xxSuccessful() && forEntity.getBody() != null) {

			ByteArrayResource merged = this.initializrSupport.repackStarter(forEntity.getBody());

			return ResponseEntity.ok()
				.headers(DownloadUtil.headers("starter.zip"))
				.contentLength(merged.contentLength())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(merged);
		}

		return ResponseEntity.internalServerError().build();
	}

}
