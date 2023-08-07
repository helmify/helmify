package com.start.helm.domain.maven;

import com.start.helm.domain.dependency.DependencyFetcher;
import com.start.helm.domain.helm.HelmContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.api.model.Dependency;
import org.apache.maven.api.model.Model;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MavenModelProcessor {

  private final DependencyFetcher dependencyFetcher;

  private final List<DependencyResoler> dependencyMatchers = new ArrayList<>();

  public MavenModelProcessor(DependencyFetcher dependencyFetcher) {
    this.dependencyFetcher = dependencyFetcher;
    this.dependencyMatchers.addAll(List.of(
        new SpringBootStarterWebResolver(),
        new SpringBootStarterAmqpResolver(this.dependencyFetcher)
    ));

  }


  public HelmContext process(Model m) {
    List<Dependency> dependencies = m.getDependencies();

    HelmContext context = new HelmContext();

    dependencies.stream()
        .filter(d -> !"test".equals(d.getScope()))
        .map(d -> dependencyMatchers
            .stream()
            .filter(matcher -> matcher.matches(d.getArtifactId()))
            .findFirst()
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(d -> d.updateHelmContext(context));

    log.info("Helm context: {}", context);
    return context;

  }

  interface DependencyResoler {
    List<String> matchOn();

    default boolean matches(String artifactId) {
      return matchOn().stream().anyMatch(artifactId::equals);
    }

    void updateHelmContext(HelmContext context);

    default String lookupString() {
      return "";
    }

  }

  class SpringBootStarterWebResolver implements DependencyResoler {

    @Override
    public List<String> matchOn() {
      return List.of("spring-boot-starter-web", "spring-boot-starter-webflux", "spring-boot-starter-graphql");
    }

    @Override
    public void updateHelmContext(HelmContext context) {
      context.setCreateIngress(true);
    }
  }

  @RequiredArgsConstructor
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

}
