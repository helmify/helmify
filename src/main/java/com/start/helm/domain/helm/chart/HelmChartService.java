package com.start.helm.domain.helm.chart;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.providers.HelmChartYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmConfigMapProvider;
import com.start.helm.domain.helm.chart.providers.HelmDeploymentYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmHelperProvider;
import com.start.helm.domain.helm.chart.providers.HelmHpaYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmIgnoreProvider;
import com.start.helm.domain.helm.chart.providers.HelmIngressYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmNotesProvider;
import com.start.helm.domain.helm.chart.providers.HelmServiceAccountYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmServiceYamlProvider;
import com.start.helm.domain.helm.chart.providers.HelmValuesYamlProvider;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Service for generating helm charts based on input
 */
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
  private final HelmIgnoreProvider ignoreProvider;
  private final HelmConfigMapProvider configMapProvider;

  @SneakyThrows
  private void writeZip(HelmChartModel model) {
    FileOutputStream fileOutputStream = new FileOutputStream("helm.zip");
    ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

    ZipEntry zipEntry = new ZipEntry("Chart.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.chartYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("values.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.valuesYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry(".helmignore");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.ignore.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/_helpers.tpl");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.helperTpl.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/deployment.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.deploymentYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/hpa.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.hpaYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/ingress.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.ingressYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/service.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.serviceYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/serviceaccount.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.serviceAccountYaml.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/NOTES.txt");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.notes.getBytes());
    zipOutputStream.closeEntry();

    zipEntry = new ZipEntry("templates/configmap.yaml");
    zipOutputStream.putNextEntry(zipEntry);
    zipOutputStream.write(model.configMap.getBytes());
    zipOutputStream.closeEntry();


    zipOutputStream.close();
    fileOutputStream.close();

  }

  public void process(HelmContext context) {
    HelmChartModel model = new HelmChartModel();

    model.context = context;

    model.chartYaml = helmChartYamlProvider.getFileContent(context);
    model.valuesYaml = valuesYamlProvider.getFileContent(context);
    model.serviceYaml = serviceYamlProvider.getFileContent(context);
    model.serviceAccountYaml = serviceAccountYamlProvider.getFileContent(context);
    model.ingressYaml = ingressYamlProvider.getFileContent(context);
    model.hpaYaml = hpaProvider.getFileContent(context);
    model.deploymentYaml = deploymentProvider.getFileContent(context);
    model.helperTpl = helperProvider.getFileContent(context);
    model.notes = notesProvider.getFileContent(context);
    model.ignore = ignoreProvider.getFileContent(context);
    model.configMap = configMapProvider.getFileContent(context);

    log.info("Chart.yaml: {}", model.chartYaml);
    log.info("values.yaml: {}", model.valuesYaml);
    log.info("service.yaml: {}", model.serviceYaml);
    log.info("serviceAccount.yaml: {}", model.serviceAccountYaml);
    log.info("ingress.yaml: {}", model.ingressYaml);
    log.info("hpa.yaml: {}", model.hpaYaml);
    log.info("deployment.yaml: {}", model.deploymentYaml);
    log.info("helper.tpl: {}", model.helperTpl);
    log.info("notes.txt: {}", model.notes);
    log.info(".helmignore: {}", model.ignore);
    log.info("configmap.yaml: {}", model.configMap);

    writeZip(model);

  }


  public class HelmChartModel {
    HelmContext context;
    String chartYaml;
    String valuesYaml;
    String serviceYaml;
    String serviceAccountYaml;
    String ingressYaml;
    String hpaYaml;
    String deploymentYaml;
    String helperTpl;
    String notes;
    String ignore;
    String configMap;
  }


}
