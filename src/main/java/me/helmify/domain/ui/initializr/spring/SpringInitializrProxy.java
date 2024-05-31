package me.helmify.domain.ui.initializr.spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.ui.ZipFileService;
import me.helmify.domain.ui.initializr.InitializrSupport;
import me.helmify.domain.ui.upload.CompositeFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
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
public class SpringInitializrProxy extends InitializrSupport {

	@Value("${spring.initializr.host}")
	private String initializrHost;

	public SpringInitializrProxy(CompositeFileUploadService fileUploadService, ZipFileService zipFileService) {
		super(fileUploadService, zipFileService);
	}

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
	public void getStarter(@RequestParam("artifactId") String artifactId, @RequestParam("version") String version,
			@RequestParam(name = "chartFlavor", defaultValue = "helm") String chartFlavor, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Map<String, List<String>> collected = request.getParameterMap()
			.keySet()
			.stream()
			.collect(Collectors.toMap(k -> k, k -> Arrays.asList(request.getParameterMap().get(k))));

		URI uri = UriComponentsBuilder.fromHttpUrl(getInitializrHost() + "starter.zip")
			.queryParams(new MultiValueMapAdapter<>(collected))
			.build()
			.toUri();

		RestClient r = RestClient.builder().build();

		byte[] originalStarter = r.get().uri(uri).retrieve().body(new ParameterizedTypeReference<byte[]>() {
		});

		response.setHeader("Content-Disposition", "attachment; filename=starter.zip");
		response.setContentType("application/octet-stream");
		streamStarter(originalStarter, response, artifactId, version, "starter.zip", chartFlavor);

	}

}
