package com.start.helm.domain.dependency;

import com.start.helm.domain.artifacthub.ArtifactHubResponse;
import com.start.helm.domain.helm.HelmDependency;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ArtifactHubDependencyFetcher implements DependencyFetcher {

  private final RestTemplate template;

  private static final String searchUrl =
      "https://artifacthub.io/api/v1/packages/search?ts_query_web=%s&facets=false&sort=relevance&limit=5&offset=0";

  public Optional<HelmDependency> findDependency(String name) {
    ResponseEntity<ArtifactHubResponse> exchange =
        template.exchange(String.format(searchUrl, name), HttpMethod.GET, null, ArtifactHubResponse.class);
    return Optional
        .ofNullable(exchange.getBody())
        .map(ArtifactHubResponse::packages)
        .orElse(List.of())
        .stream()
        .filter(p -> p.repository().verifiedPublisher()).findFirst()
        .map(p -> new HelmDependency(p.name(), p.version(), p.repository().url()));
  }

}
