package com.start.helm.domain.helm.chart;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.start.helm.domain.helm.HelmChartSlice;
import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.maven.MavenModelParser;
import com.start.helm.domain.maven.MavenModelProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    Optional<Model> model = MavenModelParser.parsePom(
        new MockMultipartFile("pom-with-rabbit.xml", "pom-with-rabbit.xml", "text/plain",
            getClass().getClassLoader().getResourceAsStream("pom-with-rabbit.xml")
        )
    );
    assertTrue(model.isPresent());
    Model m = model.get();
    HelmContext context = mavenModelProcessor.process(m);

    context.setAppName("test");
    context.setAppVersion("1.0.0");

    assertNotNull(context);
    assertEquals(1, context.getHelmChartSlices().size());
    assertEquals(1, context.getValuesGlobalBlocks().size());
    assertEquals(1, context.getHelmDependencies().size());

    HelmChartSlice fragment = context.getHelmChartSlices().iterator().next();
    assertAll(
        () -> assertNotNull(fragment.getPreferredChart()),
        () -> assertNotNull(fragment.getValuesEntries()),
        () -> assertNotNull(fragment.getValuesEntries().get("global")),
        () -> assertNotNull(fragment.getValuesEntries().get("rabbitmq")),
        () -> assertNotNull(fragment.getSecretEntries()),
        () -> assertNotNull(fragment.getDefaultConfig()),
        () -> assertNotNull(fragment.getEnvironmentEntries()),
        () -> assertNotNull(fragment.getInitContainer())
    );

    service.process(context);
    // briefly wait
    Thread.sleep(1000);
    File f = new File("helm.zip");
    assertTrue(f.exists());
    assertTrue(f.length() > 0);
    ZipFile zipFile = new ZipFile(f);
    Enumeration<? extends ZipEntry> entries = zipFile.entries();

    List<String> names = new ArrayList<>();

    while (entries.hasMoreElements()) {
      ZipEntry zipEntry = entries.nextElement();
      String name = zipEntry.getName();
      names.add(name);
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

  }
}
