package me.helmify.domain.ui.initializr;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.ui.ZipFileService;
import me.helmify.domain.ui.upload.CompositeFileUploadService;
import me.helmify.util.ZipUtil;
import org.springframework.core.ParameterizedTypeReference;

import java.io.ByteArrayInputStream;

@RequiredArgsConstructor
public class InitializrSupport {

	protected final CompositeFileUploadService fileUploadService;

	protected final ZipFileService zipFileService;

	protected static final ParameterizedTypeReference<byte[]> byteArrayType = new ParameterizedTypeReference<>() {
	};

	protected static final ParameterizedTypeReference<Object> objectType = new ParameterizedTypeReference<>() {
	};

	protected void streamStarter(byte[] originalStarter, HttpServletResponse response, String artifactId,
			String version, String filename, String chartFlavor) {
		if (originalStarter != null) {
			String buildFile = ZipUtil.tryReadBuildFile(new ByteArrayInputStream(originalStarter)).orElseThrow();
			HelmContext helmContext = fileUploadService.processBuildfile(buildFile, artifactId, version);
			helmContext.setChartFlavor(chartFlavor);
			zipFileService.streamZip(helmContext, originalStarter, response, filename);
		}
	}

}
