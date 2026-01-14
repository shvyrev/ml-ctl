package io.cx.ml.cli.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class EndpointUpdateRequestExtended {
    private String name;
    private String description;
    private GpuFlavor flavor = GpuFlavor.COMMUNAL;
}