package me.helmify.domain.ui.controllers.rest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.ui.ZipFileService;
import me.helmify.domain.ui.upload.CompositeFileUploadService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CliRestController {

	private final CompositeFileUploadService fileUploadService;

	private final ZipFileService zipFileService;

	@PostMapping("/cli")
	public void cli(@RequestParam("name") String name, @RequestParam("version") String version,
			@RequestParam(name = "chartFlavor", defaultValue = "helm") String chartFlavor,
			@RequestBody String buildFileContents, HttpServletResponse response) throws IOException {

		HelmContext helmContext = fileUploadService.processBuildfile(buildFileContents, name, version);
		helmContext.setChartFlavor(chartFlavor);
		zipFileService.streamZip(helmContext, response, "helm.zip");
	}

}
