package io.cx.ml.cli.commands;

import io.cx.ml.cli.commands.storage.*;
        import picocli.CommandLine.Command;

@Command(name = "storage",
        aliases = {"fs"},
        description = "Управление файлами и папками (Artifact Store)",
        subcommands = {
                ListFolderCommand.class,   // ls
                CreateFolderCommand.class, // mkdir
                DeleteFolderCommand.class, // rmdir (здесь Long ID)
                UploadCommand.class,       // up
                MupCommand.class,          // mup
                RmCommand.class            // rm (здесь по UUID)
        })
public class StorageCommand {
}