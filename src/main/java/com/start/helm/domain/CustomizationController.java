package com.start.helm.domain;

import static com.start.helm.JsonUtil.fromJson;

import com.start.helm.domain.helm.HelmContext;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for customizing a {@link HelmContext}.
 * <p/>
 * This is the second controller to be called by the client. Here we receive the few
 * missing pieces we want to fill in on the Helm Chart, so we can offer a fully
 * populated Helm Chart to the user in the next step.
 */
@Slf4j
@Controller
public class CustomizationController {

  /**
   * Endpoint for handling customization of an existing {@link HelmContext}.
   * <p/>
   * Here we parse an existing {@link HelmContext} from the request body and
   * set some properties on it after the user has entered data. Then we just
   * put the updated {@link HelmContext} back into the View Model: {@link Model}
   */
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
      viewModel.addAttribute("helmContext", helmContext);
    }

    viewModel.addAttribute("customized", true);

    return "fragments :: pom-upload-form";
  }


}
