package io.cx.ml.cli.commands.endpoints;

import io.cx.ml.cli.clients.EndpointClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.util.UUID;

@CommandLine.Command(name = "rm", description = "Удалить эндпоинт")
public class EndpointDeleteCommand implements Runnable {

    @Inject
    @RestClient
    EndpointClient client;

    @CommandLine.Parameters(index = "0", description = "UUID эндпоинта")
    UUID endpointId;

    @Override
    public void run() {
        System.out.println("Удаление эндпоинта " + endpointId + "...");
        try {
            client.deleteEndpoint(endpointId).await().indefinitely();
            System.out.println("Эндпоинт успешно удален.");
        } catch (Exception e) {
            System.err.println("Не удалось удалить эндпоинт: " + e.getMessage());
        }
    }
}