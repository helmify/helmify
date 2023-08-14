package com.start.helm.domain.helm.chart;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.providers.HelmFileProvider;
import java.io.FileOutputStream;
import java.util.List;
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

  private final List<HelmFileProvider> providers;

  @SneakyThrows
  public void process(HelmContext context) {

    log.info("Got {} providers to process", providers.size());

    FileOutputStream fileOutputStream = new FileOutputStream("helm.zip");
    ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
    zipOutputStream.putNextEntry(new ZipEntry("templates/"));
    zipOutputStream.closeEntry();

    providers.forEach(p -> {
      String fileContent = p.getFileContent(context);
      String fileName = p.getFileName();
      log.info("Adding File {} with content {}", fileName, fileContent);
      addZipEntry(fileName, fileContent, zipOutputStream);
    });

    zipOutputStream.close();
    fileOutputStream.close();

  }

  private void addZipEntry (String filename, String content, ZipOutputStream zipOutputStream) {
    ZipEntry zipEntry = new ZipEntry(filename);
    try {
      zipOutputStream.putNextEntry(zipEntry);
      zipOutputStream.write(content.getBytes());
      zipOutputStream.closeEntry();
    } catch (Exception e) {
      log.error("Error while processing file: {}", filename, e);
    }
  }

}
