package com.start.helm.domain.ui;

import com.start.helm.domain.ChartCountTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for index page.
 * <p>
 * Users visiting the application will talk to this controller first.
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

	private final ChartCountTracker chartCountTracker;

	private final BuildInfoProvider buildInfoProvider;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "hello world");
		model.addAttribute("chartsGenerated", chartCountTracker.getChartCount());
		model.addAttribute("buildInfo", buildInfoProvider.getBuildInfo());
		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

}
