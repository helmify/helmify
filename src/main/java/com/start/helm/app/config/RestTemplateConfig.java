package com.start.helm.app.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder().setReadTimeout(java.time.Duration.ofSeconds(10))
			.setConnectTimeout(java.time.Duration.ofSeconds(10))
			.build();
	}

}
