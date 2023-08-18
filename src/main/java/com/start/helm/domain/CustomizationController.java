package com.start.helm.domain;

import static com.start.helm.JsonUtil.fromJson;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.HelmChartService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomizationController {

  private final HelmChartService helmChartService;

  @PostMapping("/customize")
  public String customize(@RequestBody Map<String, Object> map, Model viewModel) {
    log.info("Customization request: {}", map);
    if (map.containsKey("helmContext")) {
      String json = map.get("helmContext").toString();
      HelmContext helmContext = fromJson(json, HelmContext.class);
      helmContext.setCustomized(true);

      helmContext.setCustomizations(new HelmContext.HelmContextCustomization(
          map.get("dockerImageRepositoryUrl").toString(),
          map.getOrDefault("dockerImageTag", "latest").toString(),
          map.getOrDefault("dockerImagePullSecret", "").toString(),
          Map.of()
      ));

      helmContext.setZipLink("helm.zip");
      helmChartService.process(helmContext);
      viewModel.addAttribute("helmContext", helmContext);
    }

    viewModel.addAttribute("customized", true);


    return "fragments :: pom-upload-form";
  }


}
