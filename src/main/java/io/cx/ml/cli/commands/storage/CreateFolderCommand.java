package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.clients.FolderClient;
import io.cx.ml.cli.dto.FileInfoResponse;
import io.cx.ml.cli.dto.FolderCreateRequest;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@RegisterForReflection
@Command(name = "mb", description = "Создать новую папку")
public class CreateFolderCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Имя новой папки")
    private String folderName;

    @Option(names = {"-p", "--parent"}, description = "ID родительской папки (по умолчанию 0 - корень)", defaultValue = "-1")
    private Long parentId;

    @Inject
    @RestClient
    FolderClient folderClient;

    @Override
    public Integer call() {
        try {
            FolderCreateRequest request = new FolderCreateRequest();
            request.setName(folderName);
            request.setParentId(parentId == -1 ? null : parentId); // Если -1, сервер обычно ждет null для корня

            FileInfoResponse response = folderClient.createFolder(request);

            System.out.println("Папка успешно создана!");
            System.out.println("ID:   " + response.getId());
            System.out.println("Name: " + response.getOriginalName());

            return 0;
        } catch (Exception e) {
            System.err.println("Ошибка при создании папки: " + e.getMessage());
            return 1;
        }
    }
}