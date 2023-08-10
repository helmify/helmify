package com.start.helm.domain.helm.chart.providers;

import com.start.helm.domain.helm.HelmContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelmHelperProvider implements HelmFileProvider{

  private static final String template = """
    {{/*
    Expand the name of the chart.
    */}}
    {{- define "REPLACEME.name" -}}
    {{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
    {{- end }}
    
    {{/*
    Create a default fully qualified app name.
    We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
    If release name contains chart name it will be used as a full name.
    */}}
    {{- define "REPLACEME.fullname" -}}
    {{- if .Values.fullnameOverride }}
    {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
    {{- else }}
    {{- $name := default .Chart.Name .Values.nameOverride }}
    {{- if contains $name .Release.Name }}
    {{- .Release.Name | trunc 63 | trimSuffix "-" }}
    {{- else }}
    {{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
    {{- end }}
    {{- end }}
    {{- end }}
    
    {{/*
    Create chart name and version as used by the chart label.
    */}}
    {{- define "REPLACEME.chart" -}}
    {{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
    {{- end }}
    
    {{/*
    Common labels
    */}}
    {{- define "REPLACEME.labels" -}}
    helm.sh/chart: {{ include "REPLACEME.chart" . }}
    {{ include "REPLACEME.selectorLabels" . }}
    {{- if .Chart.AppVersion }}
    app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
    {{- end }}
    app.kubernetes.io/managed-by: {{ .Release.Service }}
    {{- end }}
    
    {{/*
    Selector labels
    */}}
    {{- define "REPLACEME.selectorLabels" -}}
    app.kubernetes.io/name: {{ include "REPLACEME.name" . }}
    app.kubernetes.io/instance: {{ .Release.Name }}
    {{- end }}
    
    {{/*
    Create the name of the service account to use
    */}}
    {{- define "REPLACEME.serviceAccountName" -}}
    {{- if .Values.serviceAccount.create }}
    {{- default (include "REPLACEME.fullname" .) .Values.serviceAccount.name }}
    {{- else }}
    {{- default "default" .Values.serviceAccount.name }}
    {{- end }}
    {{- end }}
      """;

  @Override
  public String getFileContent(HelmContext context) {
    return template.replace("REPLACEME", context.getAppName());
  }

}
