package io.cx.ml.cli.dto;

public record GpuFlavorsDto(
        String value,
        String model,
        String params,
        String resources,
        String description
) {
}
