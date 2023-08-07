package com.start.helm.domain.helm;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class HelmContext {

  @Getter
  @Setter
  private boolean createIngress = false;

  @Getter
  private final Set<HelmDependency> helmDependencies = new HashSet<>();

  public void addHelmDependency(HelmDependency helmDependency) {
    this.helmDependencies.add(helmDependency);
  }

}
