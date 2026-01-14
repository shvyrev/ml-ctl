package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.clients.FolderClient;
import io.cx.ml.cli.dto.FileInfoResponse;
import io.cx.ml.cli.dto.ListResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Slf4j
@RegisterForReflection
@Command(name = "ls", description = "Список файлов и папок")
public class ListFolderCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "ID папки (0 для корня)", defaultValue = "-1")
    private Long folderId;

    @Inject
    @RestClient
    FolderClient folderClient;

    @Override
    public Integer call() {
        try {
            // Если folderId = 0, вызываем корневой метод (или передаем null/спец. значение)
            ListResponse<FileInfoResponse> response;

            // Логика выбора метода
            if (folderId == null || folderId == -1L) {
                response = folderClient.getRootContents(0, 100);
            } else {
                response = folderClient.getFolderContents(folderId, 0, 100);
            }

            System.out.println("Содержимое папки ID: " + (folderId == 0 ? "ROOT" : folderId));
            System.out.println("----------------------------------------------------------------------");
            System.out.printf("%-10s | %-30s | %-15s | %s%n", "TYPE", "NAME", "SIZE", "PATH");
            System.out.println("----------------------------------------------------------------------");

            for (FileInfoResponse item : response.getData()) {

                String type = item.getIsFolder() ? "[DIR]" : "FILE";
                String size = item.getIsFolder() ? "-" : formatSize(item.getSize());

                System.out.printf("%-10s | %-30s | %-15s | %s%n",
                        type,
                        truncate(item.getOriginalName(), 30),
                        size,
                        item.getPath());
            }

            System.out.println("----------------------------------------------------------------------");
            System.out.printf("Всего элементов: %d%n", response.getPaging().totalItems());

            return 0;
        } catch (Exception e) {
            System.err.println("Ошибка при получении списка: " + e.getMessage());
            return 1;
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }
}