package com.start.helm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonUtil {

  private static final ObjectMapper mapper = new ObjectMapper();

  @SneakyThrows
  public static String toJson(Object object) {
    return mapper.writeValueAsString(object);
  }

  @SneakyThrows
  public static <T> T fromJson(String json, Class<T> type) {
    return mapper.readValue(json, type);
  }

}
