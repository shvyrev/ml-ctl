package io.cx.ml.cli.commands;

import io.cx.ml.cli.commands.endpoints.EndpointCreateCommand;
import io.cx.ml.cli.commands.endpoints.EndpointDeleteCommand;
import io.cx.ml.cli.commands.endpoints.EndpointFlavorsCommand;
import io.cx.ml.cli.commands.endpoints.EndpointListCommand;
import picocli.CommandLine.Command;

@Command(name = "endpoint",
        aliases = {"e"},
        description = "Управление эндпойнтами моделей (Serving API)",
        subcommands = {
                EndpointListCommand.class,
                EndpointCreateCommand.class,
                EndpointDeleteCommand.class,
                EndpointFlavorsCommand.class
        })
public class EndpointCommand { }