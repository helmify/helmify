package com.start.helm.domain.helm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HelmChartSliceBuilder {

	default List<Map<String, Object>> getEnvironmentEntries(HelmContext context) {
		return new ArrayList<>();
	}

	default Map<String, String> getDefaultConfig() {
		return new HashMap<>();
	}

	default Map<String, String> getPreferredChart() {
		return new HashMap<>();
	}

	default Map<String, Object> getValuesEntries(HelmContext context) {
		return new HashMap<>();
	}

	default Map<String, Object> getSecretEntries() {
		return new HashMap<>();
	}

	default String initContainerCheckEndpoint(HelmContext context) {
		return "{{ .Values.global.hosts.%s }} {{ .Values.global.ports.%s }}";
	}

}
