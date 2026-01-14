package io.cx.ml.cli.commands.models;

import io.cx.ml.cli.clients.ArtifactStoreClient;
import io.cx.ml.cli.dto.StoreModelRequest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

@CommandLine.Command(name = "sm", description = "Подготовить модель для Triton")
public class StoreModelCommand implements Runnable {

    @Inject
    @RestClient
    ArtifactStoreClient client;

    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "Имя модели")
    String modelName;

    @CommandLine.Option(names = {"-p", "--path"}, required = true, description = "Путь к папке")
    String folderPath;

    @Override
    public void run() {
        var request = new StoreModelRequest(modelName, folderPath);
        System.out.println("Отправка запроса на подготовку модели: " + modelName);

        try {
            // Блокируем поток выполнения CLI до получения ответа
            client.storeModel(request)
                    .await().indefinitely();

            System.out.println("Успешно! Модель будет готова в ближайшее время.");
        } catch (Exception e) {
            System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }
}