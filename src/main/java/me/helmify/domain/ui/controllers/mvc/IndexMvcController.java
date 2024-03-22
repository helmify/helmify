package me.helmify.domain.ui.controllers.mvc;

import me.helmify.domain.ui.buildinfo.BuildInfoProvider;
import me.helmify.domain.ui.counter.ChartCounter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for index page.
 * <p>
 * Users visiting the application will talk to this controller first.
 */
@Controller
public class IndexMvcController {

	private final ChartCounter chartCounter;

	private final BuildInfoProvider buildInfoProvider;

	private final String supportedDependencies;

	public IndexMvcController(ChartCounter chartCounter, BuildInfoProvider buildInfoProvider,
			@Qualifier("supportedDependencies") String supportedDependencies) {
		this.chartCounter = chartCounter;
		this.buildInfoProvider = buildInfoProvider;
		this.supportedDependencies = supportedDependencies;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("chartsGenerated", chartCounter.getChartCount());
		model.addAttribute("buildInfo", buildInfoProvider.getBuildInfo());
		model.addAttribute("supportedDependencies", supportedDependencies);
		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "privacy";
	}

}
