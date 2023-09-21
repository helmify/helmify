package com.start.helm.util;

import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

  @SneakyThrows
  public static ByteArrayResource merge(ZipInputStream original, ZipInputStream additonal, String parentDirName) {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ZipOutputStream zipOutputStream = new ZipOutputStream(out);

    ZipEntry e;
    while ((e = original.getNextEntry()) != null) {
      zipOutputStream.putNextEntry(e);
      //TOOO zipOutputStream.write();
      zipOutputStream.closeEntry();
    }

    zipOutputStream.putNextEntry(new ZipEntry(parentDirName + "/"));
    zipOutputStream.closeEntry();

    while ((e = additonal.getNextEntry()) != null) {
      ZipEntry zipEntry = new ZipEntry(parentDirName + "/" + e.getName());
      zipOutputStream.putNextEntry(zipEntry);
      //TOOO zipOutputStream.write();
      zipOutputStream.closeEntry();
    }

    zipOutputStream.close();
    return new ByteArrayResource(out.toByteArray());
  }

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
