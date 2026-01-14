package io.cx.ml.cli.commands.storage;

import picocli.CommandLine;

public class TargetOptions {

    @CommandLine.Option(names = {"-f", "--folder"},
            description = "ID базовой папки (от которой считается path)",
            defaultValue = "0")
    public Long folderId;

    @CommandLine.Option(names = {"-p", "--path"},
            description = "Путь в облаке (например, 'models/qwen'). Если начинается с '/', считается от корня.")
    public String cloudPath;

    // Удобный метод для проверки, задано ли хоть что-то
    public boolean isEmpty() {
        return (folderId == null || folderId == 0) && (cloudPath == null || cloudPath.isBlank());
    }
}