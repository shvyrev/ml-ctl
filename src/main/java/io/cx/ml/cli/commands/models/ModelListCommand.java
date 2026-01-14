package io.cx.ml.cli.commands.models;

import io.cx.ml.cli.clients.ModelServingClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

@CommandLine.Command(name = "ls", description = "Показать список моделей")
public class ModelListCommand implements Runnable {
    @Inject
    @RestClient
    ModelServingClient client;

    @Override
    public void run() {
        client.listModels(0, 50)
                .onItem().invoke(response -> {
                    System.out.printf("%-40s | %-20s | %-10s%n", "ID", "Name", "Status");
                    System.out.println("-".repeat(75));
                    response.getData().forEach(m ->
                            System.out.printf("%-40s | %-20s | %-10s%n", m.getModelId(), m.getModelName(), m.getStatus()));
                })
                .await().indefinitely();
    }
}
