package me.helmify.domain.helm.chart.providers;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.HelmContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmIngressYamlProvider implements HelmFileProvider {

	private static final String template = """
			{{- if .Values.ingress.enabled -}}
			{{- $fullName := include "%s.fullname" . -}}
			{{- $svcPort := .Values.service.port -}}
			{{- if and .Values.ingress.className (not (semverCompare ">=1.18-0" .Capabilities.KubeVersion.GitVersion)) }}
			  {{- if not (hasKey .Values.ingress.annotations "kubernetes.io/ingress.class") }}
			  {{- $_ := set .Values.ingress.annotations "kubernetes.io/ingress.class" .Values.ingress.className}}
			  {{- end }}
			{{- end }}
			{{- if semverCompare ">=1.19-0" .Capabilities.KubeVersion.GitVersion -}}
			apiVersion: networking.k8s.io/v1
			{{- else if semverCompare ">=1.14-0" .Capabilities.KubeVersion.GitVersion -}}
			apiVersion: networking.k8s.io/v1beta1
			{{- else -}}
			apiVersion: extensions/v1beta1
			{{- end }}
			kind: Ingress
			metadata:
			  name: {{ $fullName }}
			  labels:
			    {{- include "%s.labels" . | nindent 4 }}
			  {{- with .Values.ingress.annotations }}
			  annotations:
			    {{- toYaml . | nindent 4 }}
			  {{- end }}
			spec:
			  {{- if and .Values.ingress.className (semverCompare ">=1.18-0" .Capabilities.KubeVersion.GitVersion) }}
			  ingressClassName: {{ .Values.ingress.className }}
			  {{- end }}
			  {{- if .Values.ingress.tls }}
			  tls:
			    {{- range .Values.ingress.tls }}
			    - hosts:
			        {{- range .hosts }}
			        - {{ tpl (tpl .host $) $ | quote }}
			        {{- end }}
			      secretName: {{ .secretName }}
			    {{- end }}
			  {{- end }}
			  rules:
			    {{- range .Values.ingress.hosts }}
			    - host: {{ tpl (tpl .host $) $ | quote }}
			      http:
			        paths:
			          {{- range .paths }}
			          - path: {{ .path }}
			            {{- if and .pathType (semverCompare ">=1.18-0" $.Capabilities.KubeVersion.GitVersion) }}
			            pathType: {{ .pathType }}
			            {{- end }}
			            backend:
			              {{- if semverCompare ">=1.19-0" $.Capabilities.KubeVersion.GitVersion }}
			              service:
			                name: {{ $fullName }}
			                port:
			                  number: {{ $svcPort }}
			              {{- else }}
			              serviceName: {{ $fullName }}
			              servicePort: {{ $svcPort }}
			              {{- end }}
			          {{- end }}
			    {{- end }}
			{{- end }}
			  """;

	@Override
	public String getFileContent(HelmContext context) {
		return String.format(template, context.getAppName(), context.getAppName());
	}

	@Override
	public String getFileName() {
		return "templates/ingress.yaml";
	}

}
