package com.start.helm.domain.helm.chart.customizers;

import com.start.helm.domain.helm.HelmContext;

public interface TemplateCustomizer {

  String customize(String template, HelmContext context);

}
