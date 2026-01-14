package io.cx.ml.cli.commands.models;

import io.cx.ml.cli.clients.ModelServingClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;
import java.util.UUID;

@CommandLine.Command(
        name = "rm",
        aliases = {"delete", "remove"},
        description = "Удалить модель по её ID"
)
public class ModelDeleteCommand implements Runnable {

    @Inject
    @RestClient
    ModelServingClient client;

    @CommandLine.Parameters(index = "0", description = "UUID модели, которую нужно удалить")
    UUID modelId;

    @CommandLine.Option(names = {"-f", "--force"}, description = "Не запрашивать подтверждение")
    boolean force;

    @Override
    public void run() {
        if (!force) {
            System.out.printf("Вы уверены, что хотите удалить модель %s? (y/N): ", modelId);
            String response = System.console().readLine();
            if (response == null || !response.equalsIgnoreCase("y")) {
                System.out.println("Удаление отменено.");
                return;
            }
        }

        System.out.println("Удаление модели " + modelId + "...");

        try {
            // Вызываем DELETE /api/v1/model/{model-id} [cite: 124]
            client.deleteModel(modelId).await().indefinitely();

            System.out.println("Модель успешно удалена.");
        } catch (Exception e) {
            // Обработка ошибок, например 404 (Not Found) или 401 (Not Authorized) [cite: 73, 75]
            System.err.println("Ошибка при удалении: " + e.getMessage());
        }
    }
}