package io.cx.ml.cli.dto;

import java.util.List;

public record TritonSupportFrameworks(String name, List<String> versions, String description) {};
