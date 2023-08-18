package com.start.helm.domain.helm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Processing Context for new Helm Chart.
 * <p>
 * Here we prepare "things to do".
 */
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
  @Setter
  private String dependencyDescriptor;


  @Getter
  private final Set<HelmDependency> helmDependencies = new HashSet<>();

  @Getter
  private final Set<HelmChartSlice> helmChartSlices = new HashSet<>();

  @Getter
  private final List<Map<String, Object>> valuesGlobalBlocks = new ArrayList<>();

  @Getter
  @Setter
  private HelmContextCustomization customizations;

  public void addValuesGlobalBlock(Map<String, Object> valuesGlobalBlock) {
    this.valuesGlobalBlocks.add(valuesGlobalBlock);
  }

  public void addHelmChartFragment(HelmChartSlice helmChartSlice) {
    this.helmChartSlices.add(helmChartSlice);
  }

  public void addHelmDependency(HelmDependency helmDependency) {
    this.helmDependencies.add(helmDependency);
  }

  public enum FrameworkVendor {
    Spring
    //Quarkus
    //Micronaut
    //...
  }

  @Getter
  @Setter
  public class HelmContextCustomization {

    private String dockerImageRepositoryUrl;
    private String dockerImageTag;
    private String dockerImagePullSecret;

    private Map<String, String> hostnames = new HashMap<>();

  }

}
