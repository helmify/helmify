package me.helmify.domain.ui;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.events.ChartDownloadedEvent;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.ui.upload.CompositeFileUploadService;
import me.helmify.initializr.ZipFileService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CliApi {

	private final CompositeFileUploadService fileUploadService;

	private final ZipFileService zipFileService;

	@PostMapping("/cli")
	public void cli(@RequestParam("name") String name, @RequestParam("version") String version,
			@RequestBody String buildFileContents, HttpServletResponse response) throws IOException {

		HelmContext helmContext = fileUploadService.processBuildfile(buildFileContents, name, version);
		zipFileService.streamZip(helmContext, response.getOutputStream());
	}

}
