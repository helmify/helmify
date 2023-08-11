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
  @Setter
  private String appName;
  @Getter
  @Setter
  private String appVersion;
  @Getter
  @Setter
  private FrameworkVendor frameworkVendor = FrameworkVendor.Spring;

  @Getter
  private final Set<HelmDependency> helmDependencies = new HashSet<>();

  public void addHelmDependency(HelmDependency helmDependency) {
    this.helmDependencies.add(helmDependency);
  }

  public enum FrameworkVendor {
    Spring
    //Quarkus
    //Micronaut
    //...
  }

}
