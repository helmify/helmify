package com.start.helm.app.config;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Value("${artifact-hub.api-key-id}")
  private String apiKeyId;

  @Value("${artifact-hub.api-key-secret}")
  private String apiKeySecret;

  @Value("${spring.application.name}")
  private String appName;


  @Bean
  public RestTemplate restTemplate() {
    RestTemplate build = new RestTemplateBuilder()
        .setReadTimeout(Duration.ofSeconds(10))
        .setConnectTimeout(Duration.ofSeconds(10))
        .build();
    build.setErrorHandler(errorHandler);
    build.setInterceptors(List.of(headerAddingRequestInterceptor));
    return build;
  }

  ClientHttpRequestInterceptor headerAddingRequestInterceptor = new ClientHttpRequestInterceptor() {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
      request.getHeaders().add("User-Agent", appName);
      request.getHeaders().add("X-API-KEY-ID", apiKeyId);
      request.getHeaders().add("X-API-KEY-SECRET", apiKeySecret);

      return execution.execute(request, body);
    }
  };

  DefaultResponseErrorHandler errorHandler = new DefaultResponseErrorHandler() {
    private Logger logger = LoggerFactory.getLogger(RestTemplate.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
      return response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
      try {
        super.handleError(response);
      } catch (Exception e) {
        logger.error("Error", e);
      }
    }
  };

}
