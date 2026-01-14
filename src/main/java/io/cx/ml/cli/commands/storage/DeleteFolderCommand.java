package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.clients.FolderClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@RegisterForReflection
@Command(name = "rm", description = "Удалить папку и всё её содержимое")
public class DeleteFolderCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "ID папки для удаления")
    private Long folderId;

    @Inject
    @RestClient
    FolderClient folderClient;

    @Override
    public Integer call() {
        try {
            // ВАЖНО: папка 0 обычно корень, сервер может запрещать её удаление
            if (folderId == null || folderId == 0) {
                System.err.println("Ошибка: Нельзя удалить корневую папку (ID 0).");
                return 1;
            }

            System.out.printf("Удаление папки ID: %d... ", folderId);

            try (Response response = folderClient.deleteFolder(folderId)) {
                if (response.getStatus() == 200 || response.getStatus() == 204) {
                    System.out.println("Успешно удалено.");
                    return 0;
                } else {
                    System.err.println("Ошибка сервера: " + response.getStatus());
                    return 1;
                }
            }
        } catch (Exception e) {
            System.err.println("\nОшибка при удалении: " + e.getMessage());
            return 1;
        }
    }
}