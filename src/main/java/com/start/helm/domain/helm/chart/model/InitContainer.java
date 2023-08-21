package com.start.helm.domain.helm.chart.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InitContainer {
  private String name;
  private String image;
  private String imagePullPolicy;
  private Map<String, Object> securityContext;
  private List<String> command;
  private Map<String, Object> resources;
}
