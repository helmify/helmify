package com.start.helm.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

@Configuration
public class YamlConfig {

    private static final Representer nonNullRepresenter = new Representer() {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            if (propertyValue == null) {
                return null;
            } else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    };
    private static final Yaml yaml = new Yaml(nonNullRepresenter, getOptions());

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
