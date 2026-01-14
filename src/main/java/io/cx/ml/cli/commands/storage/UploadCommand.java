package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.clients.FolderClient;
import io.cx.ml.cli.clients.SessionClient;
import io.cx.ml.cli.services.UploadService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@RegisterForReflection
@CommandLine.Command(name = "up", description = "Загрузить файл по протоколу сессий (чанками)")
public class UploadCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "Путь к локальному файлу")
    private String localPath;

    @CommandLine.Mixin // Picocli сама внедрит сюда -f и -p
    private TargetOptions target;

    @Inject
    @RestClient
    SessionClient sessionClient;

    @Inject
    @RestClient
    FolderClient folderClient;

    @Inject
    UploadService uploadService;

    private static final int CHUNK_SIZE = 5 * 1024 * 1024; // 5MB на чанк

    @Override
    public Integer call() {
        try {
            Path path = Path.of(localPath);
            if (!Files.exists(path)) {
                System.err.println("Файл не найден!");
                return 1;
            }

            uploadService.uploadFile(path, target.folderId, target.cloudPath);

            System.out.println("\nЗагрузка завершена успешно!");
            return 0;

        } catch (Exception e) {
            System.err.println("\nОшибка при загрузке: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}