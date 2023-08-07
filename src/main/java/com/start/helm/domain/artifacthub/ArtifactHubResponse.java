package com.start.helm.domain.artifacthub;

import java.util.List;

public record ArtifactHubResponse (List<ArtifactHubPackage> packages) {}
