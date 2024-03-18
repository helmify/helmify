package me.helmify.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class YamlConfig {

	private static final Yaml yaml = new Yaml(getOptions());

	private static DumperOptions getOptions() {
		DumperOptions options = new DumperOptions();
		options.setIndicatorIndent(2);
		options.setIndentWithIndicator(true);
		return options;
	}

	public static Yaml getInstance() {
		return yaml;
	}

	@Bean
	public Yaml yaml() {
		return yaml;
	}

}
