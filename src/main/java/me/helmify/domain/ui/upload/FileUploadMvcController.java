package me.helmify.domain.ui.upload;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.domain.ui.session.SessionInfo;
import me.helmify.domain.ui.session.SessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling upload of a pom.xml / build.gradle file.
 * <p/>
 * This is the first controller to be called by the client upon a user action. The user is
 * expected to upload a Maven pom.xml file. We parse this file and try to populate a
 * {@link HelmContext} from that.
 */
@Controller
@RequiredArgsConstructor
public class FileUploadMvcController {

	private final CompositeFileUploadService fileUploadService;

	private final SessionService sessionService;

	/**
	 * Method which handles a file upload from the client.
	 * <p/>
	 * We expect a request param called "pom" which must be a multipart file. The provided
	 * pom is parsed and a {@link HelmContext} is populated from it which is then put into
	 * the View Model: {@link Model} to expose to the client.
	 */
	@PostMapping("/upload-file")
	public String uploadFile(Model viewModel, @RequestParam("file") MultipartFile file) {

		final String fileName = file.getOriginalFilename();
		validateFilename(fileName);

		HelmContext helmContext = fileUploadService.processUpload(file);

		SessionInfo sessionInfo = SessionInfo.from(helmContext);
		sessionService.addSession(sessionInfo);
		viewModel.addAttribute("helmContext", sessionInfo);
		viewModel.addAttribute("sessionId", sessionInfo.getId());

		return "fragments :: second-form";
	}

	private static void validateFilename(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("File name is null");
		}
	}

}
