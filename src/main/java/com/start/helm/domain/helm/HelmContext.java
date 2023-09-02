package com.start.helm.domain.helm;

import com.start.helm.util.JsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Processing Context for a new Helm Chart.
 * <p>
 * This is populated based on a provided dependency descriptor, like a Maven pom.xml file.
 */
public class HelmContext {

  @Getter
  @Setter
  private boolean createIngress = false;

  @Getter
  @Setter
  private boolean hasActuator = false;

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
  @Setter
  private Boolean customized = false;


  @Getter
  private final Set<HelmDependency> helmDependencies = new HashSet<>();

  @Getter
  private final Set<HelmChartSlice> helmChartSlices = new HashSet<>();

  /**
   * List of config blocks which will be merged into values.yaml.
   * <p/>
   * i.e:
   * <pre>
   *   global:
   *     hosts:
   *       rabbitmq: rabbitmq
   *       postgresql: postgresql
   *     ports:
   *       rabbitmq: 5672
   *       postgresql: 5432
   * </pre>
   */
  @Getter
  private final List<Map<String, Object>> valuesGlobalBlocks = new ArrayList<>();

  /**
   * Any last missing data to be populated before the Helm Chart is generated.
   */
  @Getter
  @Setter
  private HelmContextCustomization customizations;

  @Getter
  @Setter
  private String zipLink;

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
  @NoArgsConstructor
  @AllArgsConstructor
  public static class HelmContextCustomization {

    private String dockerImageRepositoryUrl;
    private String dockerImageTag;
    private String dockerImagePullSecret;

    private Map<String, String> hostnames = new HashMap<>();

  }

  @Override
  public String toString() {
    return JsonUtil.toJson(this);
  }
}
