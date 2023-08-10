package com.start.helm.domain.helm.chart;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.providers.HelmChartYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmValuesYamlProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Service for generating helm charts based on input
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class HelmChartService {

  private final HelmChartYamlProvider helmChartYamlProvider;
  private final HelmValuesYamlProvider valuesYamlProvider;

  public void process(HelmContext context) {
    HelmChartModel model = new HelmChartModel();

    model.chartYaml = helmChartYamlProvider.getFileContent(context);
    model.valuesYaml = valuesYamlProvider.getFileContent(context);

    log.info("Chart.yaml: {}", model.chartYaml);
    log.info("values.yaml: {}", model.valuesYaml);


  }


  public class HelmChartModel {
    String chartYaml;
    String valuesYaml;
  }


}
