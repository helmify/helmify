package com.start.helm.domain.helm;

import com.start.helm.domain.helm.chart.model.InitContainer;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class HelmChartFragment {

  private List<Map<String, Object>> environmentEntries;
  private Map<String, String> defaultConfig;
  private Map<String, String> preferredChart;
  private Map<String, Object> valuesEntries;
  private Map<String, Object> initContainer;



}
