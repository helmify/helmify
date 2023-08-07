package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.dependency.DependencyFetcher;
import com.start.helm.domain.helm.HelmContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public
class SpringBootStarterAmqpResolver implements DependencyResoler {

  private final DependencyFetcher dependencyFetcher;

  @Override
  public List<String> matchOn() {
    return List.of("spring-boot-starter-amqp", "spring-cloud-starter-stream-rabbit");
  }

  @Override
  public String lookupString() {
    return "rabbitmq";
  }

  @Override
  public void updateHelmContext(HelmContext context) {
    dependencyFetcher.findDependency(this.lookupString()).ifPresentOrElse(
        context::addHelmDependency,
        () -> log.warn("{} dependency not found", lookupString())
    );

  }
}
