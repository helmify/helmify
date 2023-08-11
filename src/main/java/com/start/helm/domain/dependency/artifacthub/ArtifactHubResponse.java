package com.start.helm.domain.dependency.artifacthub;

import java.util.List;

public record ArtifactHubResponse (List<ArtifactHubPackage> packages) {}
