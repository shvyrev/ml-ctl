package io.cx.ml.cli.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class ModelCreateRequestExtended {
    private String displayName;

    private String modelDescription;

    private Boolean gpuEnabled;

    private String framework;

    private String frameworkVersion;

    private List<ModelVersionCreateRequestExtended> versions;
}
