package me.helmify.domain.ui.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.helm.HelmContext;
import me.helmify.app.annotations.args.HelmifySession;
import me.helmify.domain.ui.session.SessionInfo;
import me.helmify.domain.ui.session.SessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.Optional;

/**
 * Controller for customizing a {@link HelmContext}.
 * <p/>
 * This is the second controller to be called by the client. Here we receive the few
 * missing pieces we want to fill in on the Helm Chart, so we can offer a fully populated
 * Helm Chart to the user in the next step.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomizationMvcController {

	private final SessionService sessionService;

	/**
	 * Endpoint for handling customization of an existing {@link HelmContext}.
	 * <p/>
	 * Here we parse an existing {@link HelmContext} from the request body and set some
	 * properties on it after the user has entered data. Then we just put the updated
	 * {@link HelmContext} back into the View Model: {@link Model}
	 */
	@PostMapping("/customize")
	public String customize(@HelmifySession SessionInfo info, Model viewModel) {

		HelmContext context = info.getContext();
		context.setCustomizations(new HelmContext.HelmContextCustomization(info.getDockerImageRepositoryUrl(),
				Optional.ofNullable(info.getDockerImageTag()).orElse("latest"),
				Optional.ofNullable(info.getDockerImagePullSecret()).orElse(""), Map.of()));
		context.setZipLink("helm.zip");
		context.setCustomized(true);
		info.setZipLink(context.getZipLink());
		info.setContext(context);
		sessionService.addSession(info);

		viewModel.addAttribute("sessionId", info.getId());
		viewModel.addAttribute("zipLink", info.getZipLink());
		viewModel.addAttribute("customized", true);

		return "fragments :: third-form";
	}

}
