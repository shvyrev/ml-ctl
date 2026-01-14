package io.cx.ml.cli.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GpuFlavor {
    A100_SMALL("a100-small", "NVIDIA A100", "1x A100 (40 GB vRAM)", "8x vCPU / 64 GB RAM", "Быстрый инференс больших моделей, небольшое обучение."),
    A100_HIGH_MEM ("a100-high-mem", "NVIDIA A100", "1x A100 (80 GB vRAM)", "16x vCPU / 128 GB RAM", "Крупномасштабные модели (LLM), требующие максимум памяти."),
    A100_MULTI_NODE ("a100-multi", "NVIDIA A100", "4x A100 (Связанные)", "32x vCPU / 256 GB RAM", "Распределенное обучение, требовательное к вычислительным ресурсам."),
    A30_STANDARD ("a30-standard", "NVIDIA A30", "1x A30 (24 GB vRAM)", "6x vCPU / 48 GB RAM", "Оптимизированный инференс (для меньшего бюджета), среднее обучение."),
    COMMUNAL ("communal", "NVIDIA A100", "1x A100 (40 GB vRAM)", "8x vCPU / 64 GB RAM", "Быстрый инференс больших моделей, небольшое обучение.");
    private final String value;
    private final String model;
    private final String params;
    private final String resources;
    private final String description;
}
