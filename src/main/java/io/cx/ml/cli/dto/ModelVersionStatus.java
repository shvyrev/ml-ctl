package io.cx.ml.cli.dto;

public enum ModelVersionStatus {
    PENDING,        // Ожидает запуска Job
    PREPARING,      // Job запущен
    REGISTERED,     // InferenceService готов
    READY,
    FAILED,         // Развертывание провалилось
    DELETED         // Удален
}
