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
public class EndpointStatusDto {
    private String status;
    private String message;
}
