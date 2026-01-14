package io.cx.ml.cli.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class ModelVersionResponseExtended {
    private String modelName;
    private String modelDescription;
    private ModelType modelType;
    private String sourcePath;
    private String modelVersionName;
    private UUID modelId;
    private ModelVersionStatus status;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
