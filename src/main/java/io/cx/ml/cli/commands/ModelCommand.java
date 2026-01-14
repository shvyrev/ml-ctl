package io.cx.ml.cli.commands;

import io.cx.ml.cli.commands.models.ModelCreateCommand;
import io.cx.ml.cli.commands.models.ModelDeleteCommand;
import io.cx.ml.cli.commands.models.ModelListCommand;
import io.cx.ml.cli.commands.models.StoreModelCommand;
import picocli.CommandLine.Command;

@Command(name = "model",
        aliases = {"m"},
        description = "Управление моделями и версиями (Serving API)",
        subcommands = {
                ModelListCommand.class,   // ls
                ModelCreateCommand.class, // create
                ModelDeleteCommand.class, // rm (здесь UUID)
                StoreModelCommand.class   // sm (подготовка для Triton)
        })
public class ModelCommand {
}