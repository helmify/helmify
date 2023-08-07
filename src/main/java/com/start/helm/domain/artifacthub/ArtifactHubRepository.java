package com.start.helm.domain.artifacthub;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtifactHubRepository (
      String url,
      @JsonProperty("organization_name")
      String organizationName,
      @JsonProperty("verified_publisher")
      boolean verifiedPublisher
  ){}
