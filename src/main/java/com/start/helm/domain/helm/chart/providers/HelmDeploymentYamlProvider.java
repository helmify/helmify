package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
public class HelmDeploymentYamlProvider implements HelmFileProvider {

  @Override
  public String getFileContent(HelmContext context) {
    return "";
  }

}
