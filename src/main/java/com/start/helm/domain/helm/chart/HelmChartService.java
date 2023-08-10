package com.start.helm.domain.helm.chart;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.providers.HelmChartYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmDeploymentYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmHelperProvider;
import com.start.helm.domain.helm.chart.providers.HelmHpaYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmIngressYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmNotesProvider;
import com.start.helm.domain.helm.chart.providers.HelmServiceAccountYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmServiceYamlProvider;
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
  private final HelmServiceYamlProvider serviceYamlProvider;
  private final HelmServiceAccountYamlProvider serviceAccountYamlProvider;
  private final HelmIngressYamlProvider ingressYamlProvider;
  private final HelmHpaYamlProvider hpaProvider;
  private final HelmDeploymentYamlProvider deploymentProvider;
  private final HelmHelperProvider helperProvider;
  private final HelmNotesProvider notesProvider;

  public void process(HelmContext context) {
    HelmChartModel model = new HelmChartModel();

    model.chartYaml = helmChartYamlProvider.getFileContent(context);
    model.valuesYaml = valuesYamlProvider.getFileContent(context);
    model.serviceYaml = serviceYamlProvider.getFileContent(context);
    model.serviceAccountYaml = serviceAccountYamlProvider.getFileContent(context);
    model.ingressYaml = ingressYamlProvider.getFileContent(context);
    model.hpaYaml = hpaProvider.getFileContent(context);
    model.deploymentYaml = deploymentProvider.getFileContent(context);
    model.helperTpl = helperProvider.getFileContent(context);
    model.notes = notesProvider.getFileContent(context);


    log.info("Chart.yaml: {}", model.chartYaml);
    log.info("values.yaml: {}", model.valuesYaml);
    log.info("service.yaml: {}", model.serviceYaml);
    log.info("serviceAccount.yaml: {}", model.serviceAccountYaml);
    log.info("ingress.yaml: {}", model.ingressYaml);
    log.info("hpa.yaml: {}", model.hpaYaml);
    log.info("deployment.yaml: {}", model.deploymentYaml);
    log.info("helper.tpl: {}", model.helperTpl);
    log.info("notes.txt: {}", model.notes);

  }


  public class HelmChartModel {
    String chartYaml;
    String valuesYaml;
    String serviceYaml;
    String serviceAccountYaml;
    String ingressYaml;
    String hpaYaml;
    String deploymentYaml;
    String helperTpl;
    String notes;
  }


}
