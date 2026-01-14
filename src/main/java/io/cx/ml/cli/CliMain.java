package io.cx.ml.cli;

import io.cx.ml.cli.commands.MainCommand;
import io.cx.ml.cli.exceptions.GlobalExceptionHandler;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
public class CliMain implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Inject
    MainCommand mainCommand;

    @Inject
    GlobalExceptionHandler globalExceptionHandler;

    @Override
    public int run(String... args) {
        return new CommandLine(mainCommand, factory)
                .setExecutionExceptionHandler(globalExceptionHandler)
                .execute(args);
    }
}