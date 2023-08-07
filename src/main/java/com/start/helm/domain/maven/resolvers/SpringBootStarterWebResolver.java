package com.start.helm.domain.maven.resolvers;

import com.start.helm.domain.helm.HelmContext;
import java.util.List;

public class SpringBootStarterWebResolver implements DependencyResoler {

  @Override
  public List<String> matchOn() {
    return List.of("spring-boot-starter-web", "spring-boot-starter-webflux", "spring-boot-starter-graphql");
  }

  @Override
  public void updateHelmContext(HelmContext context) {
    context.setCreateIngress(true);
  }
}
