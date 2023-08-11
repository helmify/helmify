package com.start.helm.domain.dependency.artifacthub;

public record ArtifactHubPackage(
    String name,
    String version,
    ArtifactHubRepository repository
) {
}
