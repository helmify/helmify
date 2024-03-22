package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import me.helmify.util.HelmUtil;
import org.springframework.stereotype.Component;

import static me.helmify.domain.helm.chart.TemplateStringPatcher.insertAfter;

@Component
@RequiredArgsConstructor
public class HelmServiceYamlProvider implements HelmFileProvider {

	private static final String template = """
			apiVersion: v1
			kind: Service
			metadata:
			  name: {{ include "%s.fullname" . }}
			  labels:
			    {{- include "%s.labels" . | nindent 4 }}
			spec:
			  type: {{ .Values.service.type }}
			  ports:
			    - port: {{ .Values.service.port }}
			      targetPort: http
			      protocol: TCP
			      name: http
			###@helmify:healthcheckport
			  selector:
			    {{- include "%s.selectorLabels" . | nindent 4 }}
			    """;

	private static final String healthCheckPortPatch = """
			- port: {{ .Values.healthcheck.port }}
			  targetPort: {{ .Values.healthcheck.port }}
			  protocol: TCP
			  name: healthcheck
			      """;

	@Override
	public String getFileContent(HelmContext context) {
		String formatted = String.format(template, context.getAppName(), context.getAppName(), context.getAppName());
		if (context.isHasActuator()) {
			formatted = addHealthCheckPort(formatted);
		}
		return HelmUtil.removeMarkers(formatted);
	}

	private String addHealthCheckPort(String content) {
		return insertAfter(content, "###@helmify:healthcheckport", healthCheckPortPatch, 4);
	}

	@Override
	public String getFileName() {
		return "templates/service.yaml";
	}

}
