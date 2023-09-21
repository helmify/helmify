package com.start.helm.domain.helm.chart;

import com.start.helm.domain.helm.HelmContext;
import com.start.helm.domain.helm.chart.providers.HelmFileProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    return this.process(context, new ByteArrayOutputStream(), false);
  }

  @SneakyThrows
  public byte[] process(HelmContext context, ByteArrayOutputStream outputStream, boolean addParentDirectory) {
    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

    zipOutputStream.putNextEntry(new ZipEntry("templates/"));
    zipOutputStream.closeEntry();

    providers.forEach(p -> {
      String fileContent = p.getFileContent(context);
      String fileName = p.getFileName();
      addZipEntry(fileName, fileContent, zipOutputStream);
    });

    zipOutputStream.close();
    outputStream.close();

    return outputStream.toByteArray();
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
