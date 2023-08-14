package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;

public interface HelmFileProvider {

  String getFileContent(HelmContext context);

  String getFileName();

}
