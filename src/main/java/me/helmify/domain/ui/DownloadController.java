package me.helmify.domain.ui;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.ui.args.HelmifySession;
import me.helmify.domain.ui.model.SessionInfo;
import me.helmify.initializr.ZipFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for File Download.
 * <p/>
 * This controller will be called last during a user's session. To support triggering a
 * download from an AJAX call, we offer two methods, the first of which will cause HTMX to
 * perform a redirect on the client side, pointing to the second method which starts the
 * actual download.
 */
@Controller
@RequiredArgsConstructor
public class DownloadController {

	private final ZipFileService zipFileService;

	@Getter
	@Setter
	public static class DownloadRequest {

		private HelmContext helmContext;

	}

	/**
	 * Method for triggering a download. This method triggers processing of the current
	 * {@link HelmContext} and caches the resulting zip as a byte array in memory.
	 */
	@PostMapping(path = "/download")
	public ResponseEntity<?> prepareDownload(@HelmifySession SessionInfo sessionInfo) {
		return ResponseEntity.ok().header("HX-Redirect", "/download/execute?sessionId=" + sessionInfo.getId()).build();
	}

	/**
	 * Method for actually downloading a file. This method is called after a redirect is
	 * performed on the client (issued by HTMX upon receiving HX-REDIRECT header in the
	 * response).
	 */
	@GetMapping(path = "/download/execute")
	public void download(@HelmifySession SessionInfo sessionInfo, HttpServletResponse response) throws Exception {
		HelmContext context = sessionInfo.getContext();
		zipFileService.streamZip(context, response.getOutputStream());
	}

}
