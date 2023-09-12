package com.start.helm;

import java.io.BufferedInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

public class TestUtil {
  @SneakyThrows
  public static String inputStreamToString(InputStream is) {
    BufferedInputStream bis = new BufferedInputStream(is);
    byte[] bytes = IOUtils.toByteArray(bis);
    return new String(bytes);
  }

}
