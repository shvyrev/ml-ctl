package io.cx.ml.cli.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class EndpointCreateRequestExtended {
    private String name;
    private String description;
    private UUID modelId;
    private GpuFlavor flavor = GpuFlavor.COMMUNAL;
}