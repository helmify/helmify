package com.start.helm.domain.artifacthub;

public record ArtifactHubPackage(
    String name,
    String version,
    ArtifactHubRepository repository
) {
}
