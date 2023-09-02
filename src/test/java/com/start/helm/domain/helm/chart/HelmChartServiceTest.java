package com.start.helm.domain.helm.chart;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.start.helm.TestUtil;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.maven.MavenModelParser;
import com.start.helm.domain.maven.MavenModelProcessor;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.maven.api.model.Model;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class HelmChartServiceTest {

  @Autowired
  private HelmChartService service;
  @Autowired
  private MavenModelProcessor mavenModelProcessor;


  @Test
  void process() throws Exception {
    String filename = "pom-with-rabbit-postgres-web-actuator.xml";
    Optional<Model> model = MavenModelParser.parsePom(
        TestUtil.inputStreamToString(getClass().getClassLoader().getResourceAsStream(filename))
    );
    assertTrue(model.isPresent());
    Model m = model.get();
    HelmContext context = mavenModelProcessor.process(m);

    context.setAppName("test");
    context.setAppVersion("1.0.0");

    assertNotNull(context);
    assertEquals(2, context.getHelmChartSlices().size());
    assertEquals(2, context.getValuesGlobalBlocks().size());
    assertEquals(2, context.getHelmDependencies().size());
    assertTrue(context.isHasActuator());
    assertTrue(context.isCreateIngress());

    boolean rabbitmq = context.getHelmChartSlices().stream().anyMatch(s -> s.getValuesEntries().keySet().contains("rabbitmq"));
    boolean postgresql =
        context.getHelmChartSlices().stream().anyMatch(s -> s.getValuesEntries().keySet().contains("postgresql"));
    assertTrue(rabbitmq);
    assertTrue(postgresql);

    context.getHelmChartSlices().forEach(s ->
        assertAll(
            () -> assertNotNull(s.getPreferredChart()),
            () -> assertNotNull(s.getValuesEntries()),
            () -> assertNotNull(s.getValuesEntries().get("global")),
            () -> assertNotNull(s.getSecretEntries()),
            () -> assertNotNull(s.getDefaultConfig()),
            () -> assertNotNull(s.getEnvironmentEntries()),
            () -> assertNotNull(s.getInitContainer())
        )
    );

    context.setCustomizations(new HelmContext.HelmContextCustomization(
        "test", "latest", null, Map.of()
    ));
    context.setCustomized(true);

    byte[] process = service.process(context);
    ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(process));

    Files.write(Paths.get("helm.zip"), process);
    ZipFile zipFile = new ZipFile(new File("helm.zip"));

    Map<String, String> contents = new HashMap<>();
    List<String> names = new ArrayList<>();
    ZipEntry entry;
    while ((entry = zipInputStream.getNextEntry()) != null) {
      String name = entry.getName();
      names.add(name);
      InputStream inputStream = zipFile.getInputStream(entry);
      byte[] bytes = inputStream.readAllBytes();
      contents.put(name, new String(bytes));
    }

    assertAll(
        () -> assertTrue(names.contains("templates/")),
        () -> assertTrue(names.contains("Chart.yaml")),
        () -> assertTrue(names.contains("templates/configmap.yaml")),
        () -> assertTrue(names.contains("templates/deployment.yaml")),
        () -> assertTrue(names.contains("templates/_helpers.tpl")),
        () -> assertTrue(names.contains("templates/hpa.yaml")),
        () -> assertTrue(names.contains(".helmignore")),
        () -> assertTrue(names.contains("templates/ingress.yaml")),
        () -> assertTrue(names.contains("templates/NOTES.txt")),
        () -> assertTrue(names.contains("templates/secrets.yaml")),
        () -> assertTrue(names.contains("templates/serviceaccount.yaml")),
        () -> assertTrue(names.contains("templates/service.yaml")),
        () -> assertTrue(names.contains("values.yaml"))
    );

    assertTrue(contents.keySet().containsAll(names));

    checkChartYaml(context, contents);
    checkConfigMapYaml(contents);
    checkDeploymentYaml(contents);

  }

  private static void checkDeploymentYaml(Map<String, String> contents) {
    String deploymentYaml = contents.get("templates/deployment.yaml");

    // look for init containers
    assertTrue(deploymentYaml.contains("-rabbitmqchecker"));
    assertTrue(deploymentYaml.contains("-postgresqlchecker"));

    // look for env vars
    assertTrue(deploymentYaml.contains("name: SPRING_RABBITMQ_USERNAME"));
    assertTrue(deploymentYaml.contains("key: rabbitmq-username"));
    assertTrue(deploymentYaml.contains("name: SPRING_RABBITMQ_PASSWORD"));
    assertTrue(deploymentYaml.contains("key: rabbitmq-password"));

    assertTrue(deploymentYaml.contains("name: SPRING_DATASOURCE_USERNAME"));
    assertTrue(deploymentYaml.contains("key: postgres-username"));
    assertTrue(deploymentYaml.contains("name: SPRING_DATASOURCE_PASSWORD"));
    assertTrue(deploymentYaml.contains("key: postgres-password"));

    // look for probes
    assertTrue(deploymentYaml.contains("readinessProbe"));
    assertTrue(deploymentYaml.contains("livenessProbe"));
    assertTrue(deploymentYaml.contains("path: /actuator/health/liveness"));
    assertTrue(deploymentYaml.contains("path: /actuator/health/readiness"));

    // look for lifecycle hook
    assertTrue(deploymentYaml.contains("preStop:"));
    assertTrue(deploymentYaml.contains("command: [\"sh\", \"-c\", \"sleep 10\"]"));

    // look for config volume mount
    assertTrue(deploymentYaml.contains("mountPath: /workspace/BOOT-INF/classes/application.properties"));

  }

  private static void checkConfigMapYaml(Map<String, String> contents) {
    String configmapYaml = contents.get("templates/configmap.yaml");
    assertTrue(configmapYaml.contains("  application.properties: |-"));
    assertTrue(configmapYaml.contains("    spring.application.name="));
    assertTrue(configmapYaml.contains("    spring.rabbitmq.virtual-host="));
    assertTrue(configmapYaml.contains("    spring.rabbitmq.host="));
    assertTrue(configmapYaml.contains("    spring.rabbitmq.port="));
    assertTrue(configmapYaml.contains("    spring.datasource.url="));
  }

  private static void checkChartYaml(HelmContext context, Map<String, String> contents) {
    String chartYaml = contents.get("Chart.yaml");
    assertTrue(chartYaml.contains("name: " + context.getAppName()));
    assertTrue(chartYaml.contains("appVersion: " + context.getAppVersion()));
    assertTrue(chartYaml.contains("type: application"));
    assertTrue(chartYaml.contains("version: 0.1.0"));
    assertTrue(chartYaml.contains("""
          - condition: postgresql.enabled
            name: postgresql
            repository: https://charts.bitnami.com/bitnami
        """));
    assertTrue(chartYaml.contains("""
          - condition: rabbitmq.enabled
            name: rabbitmq
            repository: https://charts.bitnami.com/bitnami
        """));
  }
}
