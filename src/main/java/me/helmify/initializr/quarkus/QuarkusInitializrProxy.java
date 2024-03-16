package me.helmify.initializr.quarkus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.initializr.InitializrSupport;
import me.helmify.util.DownloadUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuarkusInitializrProxy {

	private final InitializrSupport in;

	private final RestTemplate restTemplate;

	@GetMapping(value = "/quarkus/api/streams")
	public Object getCapabilities() {
		return restTemplate.getForObject("https://code.quarkus.io/api/streams", Object.class);
	}

	@GetMapping(value = "/quarkus/api/extensions/stream/{version}")
	public Object getExtensions(@PathVariable String version, @RequestParam("platformOnly") boolean platformOnly) {
		String url = "https://code.quarkus.io/api/extensions/stream/%s?platformOnly=%s";
		return restTemplate.getForObject(String.format(url, version, platformOnly), Object.class);
	}

	@PostMapping(value = "/quarkus/api/download")
	public Object getStarter(@RequestBody Map<String, Object> body) {

		HttpEntity<Object> entity = new HttpEntity<>(body,
				new MultiValueMapAdapter<>(Map.of("Content-Type", List.of("application/json"), "Host",
						List.of("code.quarkus.io"), "User-Agent", List.of("helm-start.com"), "Accept", List.of("*/*"),
						"Accept-Encoding", List.of("gzip, deflate, br"))));
		byte[] response = restTemplate.postForObject("https://code.quarkus.io/api/download", entity, byte[].class);

		if (response != null) {

			ByteArrayResource merged = this.in.repackStarter(response);

			return ResponseEntity.ok()
				.headers(DownloadUtil.headers(body.getOrDefault("artifactId", "starter") + ".zip"))
				.contentLength(merged.contentLength())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(merged);
		}

		return ResponseEntity.internalServerError().build();
	}

	// @GetMapping(value = "/spring/starter.zip")
	// public ResponseEntity<?> getStarter(HttpServletRequest request) throws IOException
	// {
	//
	// RestTemplate restTemplate = new RestTemplate();
	//
	// Map<String, List<String>> collected = request.getParameterMap()
	// .keySet()
	// .stream()
	// .collect(Collectors.toMap(k -> k, k ->
	// Arrays.asList(request.getParameterMap().get(k))));
	//
	// URI uri = UriComponentsBuilder.fromHttpUrl("https://start.spring.io/starter.zip")
	// .queryParams(new MultiValueMapAdapter<>(collected))
	// .build()
	// .toUri();
	//
	// ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(uri, byte[].class);
	//
	// if (forEntity.getStatusCode().is2xxSuccessful() && forEntity.getBody() != null) {
	//
	// byte[] body = forEntity.getBody();
	// String uuid = UUID.randomUUID().toString();
	// Path dataDirectory = Paths.get("helm-start-data");
	// File parentDir = Paths.get(dataDirectory.toFile().getAbsolutePath(), "tmp",
	// uuid).toFile();
	// parentDir.mkdirs();
	// File helmDir = Paths.get(parentDir.getAbsolutePath(), "helm").toFile();
	// helmDir.mkdirs();
	//
	// org.zeroturnaround.zip.ZipUtil.unpack(new ByteArrayInputStream(body), parentDir);
	//
	// ByteArrayResource helmChart = this.generateHelmChart(new ByteArrayResource(body));
	//
	// org.zeroturnaround.zip.ZipUtil.unpack(helmChart.getInputStream(), helmDir);
	//
	// ByteArrayOutputStream outputStream = new ByteArrayOutputStream(body.length);
	//
	// org.zeroturnaround.zip.ZipUtil.pack(parentDir, outputStream);
	//
	// ByteArrayResource merged = new ByteArrayResource(outputStream.toByteArray());
	//
	// publisher.publishEvent(new ChartDownloadedEvent());
	//
	// return ResponseEntity.ok()
	// .headers(DownloadUtil.headers("starter.zip"))
	// .contentLength(merged.contentLength())
	// .contentType(MediaType.parseMediaType("application/octet-stream"))
	// .body(merged);
	// }
	//
	// return ResponseEntity.internalServerError().build();
	// }

}
