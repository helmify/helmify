package com.start.helm.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.SneakyThrows;

public class ZipUtil {

  @SneakyThrows
  public static Optional<String> getZipContent(String ofFile, ZipInputStream zipInputStream) {
    ZipEntry entry;

    while ((entry = zipInputStream.getNextEntry()) != null) {
      String filename = entry.getName();
      if (ofFile.equals(filename)) {
        byte[] buffer = new byte[10240];
        int len;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len = zipInputStream.read(buffer)) > 0) {
          byteArrayOutputStream.write(buffer, 0, len);
        }
        final String pomXml = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
        return Optional.of(pomXml);
      }
    }
    return Optional.empty();
  }

}
