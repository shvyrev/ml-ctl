package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.services.UploadService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.UUID;
import java.util.concurrent.Callable;

@RegisterForReflection
@CommandLine.Command(name = "rm", description = "Удалить файл в облаке по ID")
public class RmCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "UUID файла для удаления")
    private String fileId;

    @Inject
    UploadService uploadService;

    @Override
    public Integer call() {
        try {
            UUID uuid = UUID.fromString(fileId);
            uploadService.deleteFile(uuid)
                    .await().indefinitely();
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: Некорректный формат UUID.");
            return 1;
        } catch (Exception e) {
            System.err.println("Ошибка при удалении: " + e.getMessage());
            return 1;
        }
    }
}