package com.start.helm.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class StaticResourcesConfiguration {

	@Bean
	public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setOrder(Integer.MIN_VALUE); // to be first
		mapping.setUrlMap(Collections.singletonMap("/icons", iconRequestHandler()));
		mapping.setUrlMap(Collections.singletonMap("/components", componentsRequestHandler()));
		return mapping;
	}

	protected ResourceHttpRequestHandler iconRequestHandler() {
		ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		ClassPathResource classPathResource = new ClassPathResource("/static/icons");
		List<Resource> locations = Arrays.asList(classPathResource);
		requestHandler.setLocations(locations);
		return requestHandler;
	}

	protected ResourceHttpRequestHandler componentsRequestHandler() {
		ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		requestHandler.setLocations(List.of(new ClassPathResource("/static/components")));
		return requestHandler;
	}

}
