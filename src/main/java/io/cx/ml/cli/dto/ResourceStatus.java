package io.cx.ml.cli.dto;

public enum ResourceStatus {
    PENDING,
    STANDBY,
    LOADING,
    LOADED,
    FAILEDTOLOAD,
    FAILED,
    DELETED,
    READY;
}
