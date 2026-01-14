package io.cx.ml.cli.dto;

import java.util.Map;
import java.util.UUID;

public record CreateEndpointResponse(
        UUID endpointId,
        String name,
        ResourceStatus status,
        String description,
        Map<String, String> labels,
        InfrastructureResources resources) {
}
