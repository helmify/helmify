package me.helmify.initializr.quarkus;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.ui.upload.CompositeFileUploadService;
import me.helmify.initializr.InitializrSupport;
import me.helmify.initializr.ZipFileService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
public class QuarkusInitializrProxy extends InitializrSupport {

	private static final String quarkusStreamApiUrl = "https://code.quarkus.io/api/streams";

	private static final String quarkusExtensionsApiUrl = "https://code.quarkus.io/api/extensions/stream/%s?platformOnly=%s";

	private static final String quarkusDownloadUrl = "https://code.quarkus.io/api/download";

	private final RestClient r;

	public QuarkusInitializrProxy(CompositeFileUploadService fileUploadService, ZipFileService zipFileService) {
		super(fileUploadService, zipFileService);
		this.r = RestClient.builder().build();
	}

	@GetMapping(value = "/quarkus/api/streams")
	public Object getCapabilities() {
		return r.get().uri(quarkusStreamApiUrl).retrieve().body(objectType);
	}

	@GetMapping(value = "/quarkus/api/extensions")
	public Object getExtensions() {
		return r.get().uri("https://code.quarkus.io/api/extensions").retrieve().body(objectType);
	}

	@GetMapping(value = "/quarkus/api/extensions/stream/{version}")
	public Object getExtensions(@PathVariable String version, @RequestParam("platformOnly") boolean platformOnly) {
		return r.get().uri(String.format(quarkusExtensionsApiUrl, version, platformOnly)).retrieve().body(objectType);
	}

	@PostMapping(value = "/quarkus/api/download")
	public void getStarter(@RequestBody Map<String, Object> body, HttpServletResponse response) throws IOException {
		byte[] originalStarter = r.post()
			.uri(quarkusDownloadUrl)
			.body(body)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Host", "code.quarkus.io")
			.header("User-Agent", "helmify.me")
			.header("Accept", "*/*")
			.header("Accept-Encoding", "gzip, deflate, br")
			.retrieve()
			.body(byteArrayType);

		response.setHeader("Content-Disposition", "attachment; filename=starter.zip");
		response.setContentType("application/octet-stream");
		streamStarter(originalStarter, response.getOutputStream(), body.get("artifactId").toString(),
				body.get("version").toString());
	}

}
