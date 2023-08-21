package com.start.helm.domain.helm.chart;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.providers.HelmFileProvider;
import java.io.ByteArrayOutputStream;
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

  /**
   * Method for turning a populated {@link HelmContext} into a zip file
   * containing a Helm Chart. The zip file is returned as a byte array.
   */
  @SneakyThrows
  public byte[] process(HelmContext context) {

    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
    zipOutputStream.putNextEntry(new ZipEntry("templates/"));
    zipOutputStream.closeEntry();

    providers.forEach(p -> {
      String fileContent = p.getFileContent(context);
      String fileName = p.getFileName();
      addZipEntry(fileName, fileContent, zipOutputStream);
    });

    zipOutputStream.close();
    fileOutputStream.close();

    return fileOutputStream.toByteArray();
  }

  private void addZipEntry(String filename, String content, ZipOutputStream zipOutputStream) {
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
