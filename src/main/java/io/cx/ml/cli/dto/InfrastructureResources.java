package io.cx.ml.cli.dto;

import lombok.*;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InfrastructureResources {
    private String cpu;
    private String memory;
    private String gpu = "nvidia.com/gpu:1";
}
