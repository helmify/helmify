package com.start.helm.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class YamlConfig {

  private static final Yaml yaml = new Yaml(getOptions());

  public static Yaml getInstance() {
    return yaml;
  }

  private static DumperOptions getOptions() {
    DumperOptions options = new DumperOptions();
    options.setIndicatorIndent(2);
    options.setIndentWithIndicator(true);
    return options;
  }

  @Bean
  public Yaml yaml() {
    return getInstance();
  }

}
