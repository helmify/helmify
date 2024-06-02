package me.helmify.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

@Configuration
public class YamlConfig {

	private static final Yaml yaml = new Yaml(new SafeConstructor(getLoaderOptions()), new Representer(getOptions()),
			getOptions(), getLoaderOptions());

	private static DumperOptions getOptions() {
		DumperOptions options = new DumperOptions();
		options.setIndicatorIndent(2);
		options.setIndentWithIndicator(true);
		options.setProcessComments(true);
		return options;
	}

	private static LoaderOptions getLoaderOptions() {
		LoaderOptions options = new LoaderOptions();
		options.setProcessComments(true);
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
