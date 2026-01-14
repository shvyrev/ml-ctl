package io.cx.ml.cli.dto;

public record StoreModelRequest(
        String modelName,
        String filePath
) {
}
