package io.cx.ml.cli.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEndpointResponseExtended(UUID endpointId,
                                             String name,
                                             ResourceStatus status,
                                             String description,
                                             GpuFlavor gpuFlavor,
                                             LocalDateTime createdAt,
                                             LocalDateTime updatedAt) {}