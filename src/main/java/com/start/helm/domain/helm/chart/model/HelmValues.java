package com.start.helm.domain.helm.chart.model;

import com.start.helm.domain.helm.HelmContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model for Helm values.yaml
 * */
@Builder
@Getter
@Setter
public class HelmValues {

  private Integer replicaCount;

  private HelmValuesImage image;

  private List<String> imagePullSecrets;

  private String nameOverride;
  private String fullnameOverride;

  private HelmValuesServiceAccount serviceAccount;

  private Map<String, Object> podAnnotations;
  private Map<String, Object> podSecurityContext;
  private Map<String, Object> securityContext;

  private HelmValuesService service;

  private HelmValuesIngress ingress;

  private Map<String, Object> resources;

  private HelmValuesAutoscaling autoscaling;

  private Map<String, Object> nodeSelector;
  private List<?> tolerations;
  private Map<String, Object> affinity;

  public static HelmValues getDefaultHelmValues(HelmContext context) {
    return HelmValues.builder()
        .replicaCount(1)
        .image(
            new HelmValuesImage("REPLACE_REPOSITORY", "REPLACE_TAG", HelmValuesImage.ImagePullPolicy.Always, new ArrayList<>()))
        .imagePullSecrets(new ArrayList<>())
        .nameOverride("")
        .fullnameOverride(context.getAppName())
        .serviceAccount(new HelmValuesServiceAccount())
        .podAnnotations(new HashMap<>())
        .podSecurityContext(new HashMap<>())
        .securityContext(new HashMap<>())
        .service(new HelmValuesService())
        .ingress(new HelmValuesIngress())
        .resources(new HashMap<>())
        .autoscaling(new HelmValuesAutoscaling())
        .nodeSelector(new HashMap<>())
        .tolerations(new ArrayList<>())
        .affinity(new HashMap<>())
        .build();
  }

  @Getter
  @Setter
  public static class HelmValuesAutoscaling {
    private Boolean enabled = false;
    private Integer minReplicas = 1;
    private Integer maxReplicas = 100;
    private Integer targetCPUUtilizationPercentage = 80;
  }

  @Getter
  @Setter
  public static class HelmValuesIngress {
    private Boolean enabled = false;
    private String className = "";
    private Map<String, Object> annotations = new HashMap<>();
    private List<HelmValuesIngressHost> hosts = new ArrayList<>(List.of(
        new HelmValuesIngressHost()
    ));
    private List<HelmValuesIngressTls> tls = new ArrayList<>();

    @Getter
    @Setter
    public static class HelmValuesIngressTls {
      private String secretName;
      private List<String> hosts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HelmValuesIngressHost {
      private String host = "chart-example.local";
      private List<HelmValuesIngressPath> paths = new ArrayList<>(
          List.of(new HelmValuesIngressPath())
      );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelmValuesIngressPath {
      private String path = "/";
      private HelmValuesIngressPathType pathType = HelmValuesIngressPathType.ImplementationSpecific;

      public enum HelmValuesIngressPathType {
        ImplementationSpecific, Exact, Prefix
      }
    }

  }

  @Getter
  @Setter
  public static class HelmValuesService {
    private HelmValuesServiceType type = HelmValuesServiceType.ClusterIP;
    private Integer port = 8080;

    public enum HelmValuesServiceType {
      ClusterIP, NodePort, LoadBalancer, ExternalName
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HelmValuesImage {
    private String repository;
    private String tag;
    private ImagePullPolicy pullPolicy;
    private List<String> secrets;

    public enum ImagePullPolicy {
      Always, Never, IfNotPresent
    }
  }

  @Getter
  @Setter
  public static class HelmValuesServiceAccount {

    private Boolean create = false;
    private Map<String, Object> annotations = new HashMap<>();
    private String name = "";

  }


}
