package io.cx.ml.cli.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModelStatus {
    private UUID modelId;
    private String displayName;
    private ModelVersionStatus status;
}
