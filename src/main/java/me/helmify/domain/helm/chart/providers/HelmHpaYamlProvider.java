package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmHpaYamlProvider implements HelmFileProvider {

	private static final String template = """
			{{- if .Values.autoscaling.enabled }}
			apiVersion: autoscaling/v2beta1
			kind: HorizontalPodAutoscaler
			metadata:
			  name: {{ include "%s.fullname" . }}
			  labels:
			    {{- include "%s.labels" . | nindent 4 }}
			spec:
			  scaleTargetRef:
			    apiVersion: apps/v1
			    kind: Deployment
			    name: {{ include "%s.fullname" . }}
			  minReplicas: {{ .Values.autoscaling.minReplicas }}
			  maxReplicas: {{ .Values.autoscaling.maxReplicas }}
			  metrics:
			    {{- if .Values.autoscaling.targetCPUUtilizationPercentage }}
			    - type: Resource
			      resource:
			        name: cpu
			        targetAverageUtilization: {{ .Values.autoscaling.targetCPUUtilizationPercentage }}
			    {{- end }}
			    {{- if .Values.autoscaling.targetMemoryUtilizationPercentage }}
			    - type: Resource
			      resource:
			        name: memory
			        targetAverageUtilization: {{ .Values.autoscaling.targetMemoryUtilizationPercentage }}
			    {{- end }}
			{{- end }}

			  """;

	@Override
	public String getFileContent(HelmContext context) {
		return readTemplate("helm/templates/hpa.yaml").replaceAll("REPLACE_ME", context.getAppName());
	}

	@Override
	public String getFileName() {
		return "templates/hpa.yaml";
	}

}
