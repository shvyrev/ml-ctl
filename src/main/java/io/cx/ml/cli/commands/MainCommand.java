package io.cx.ml.cli.commands;

import jakarta.enterprise.context.Dependent;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Dependent
@Command(name = "ml-cli",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "CLI для взаимодействия с ML кластером",
        subcommands = {
                InitCommand.class,      // Системная: инициализация
                LoginCommand.class,     // Системная: логин
                StorageCommand.class,   // Группа: cx3:// (Artifact Store)
                ModelCommand.class,     // Группа: v1/model (Serving)
                EndpointCommand.class   // ГруппаЖ v1/endpoint (Serving)
        })
public class MainCommand implements Runnable {

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}