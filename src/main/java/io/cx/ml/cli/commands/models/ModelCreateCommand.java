package io.cx.ml.cli.commands.models;

import io.cx.ml.cli.clients.ModelServingClient;
import io.cx.ml.cli.dto.ModelCreateRequestExtended;
import io.cx.ml.cli.dto.ModelVersionCreateRequestExtended;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "create", description = "Создать новую модель")
public class ModelCreateCommand implements Runnable {

    @Inject
    @RestClient
    ModelServingClient client;

    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "Имя модели")
    String name;

    @CommandLine.Option(names = {"-f", "--framework"}, required = true, description = "Фреймворк (ONNX, PYTORCH, VLLM и т.д.)")
    String framework;

    @CommandLine.Option(names = {"-fv", "--framework-version"}, required = true, description = "Версия фреймворка (например, 1.19.0)")
    String frameworkVersion;

    @CommandLine.Option(names = {"-p", "--path"}, required = true, description = "Путь к артефакту (folderId)")
    String artifactPath;

    @Override
    public void run() {
        var req = new ModelCreateRequestExtended();
        req.setDisplayName(name);
        req.setFramework(framework);
        req.setFrameworkVersion(frameworkVersion);
        req.setGpuEnabled(false);

        var version = new ModelVersionCreateRequestExtended();
        version.setArtifactPath(artifactPath);
        req.setVersions(List.of(version));

        try {
            var result = client.createModel(req).await().indefinitely();
            System.out.println("Успешно! Модель создана. ID: " + result.getModelId());
        } catch (jakarta.ws.rs.WebApplicationException e) {
            String errorBody = e.getResponse().readEntity(String.class);
            System.err.println("Ошибка валидации сервера: " + errorBody);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}