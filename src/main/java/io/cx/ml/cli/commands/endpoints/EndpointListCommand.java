package io.cx.ml.cli.commands.endpoints;

import io.cx.ml.cli.clients.EndpointClient;
import io.cx.ml.cli.dto.CreateEndpointResponseExtended;
import io.cx.ml.cli.dto.ListResponse;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

@CommandLine.Command(name = "ls", description = "Показать список всех эндпоинтов")
public class EndpointListCommand implements Runnable {

    @Inject
    @RestClient
    EndpointClient client;

    @CommandLine.Option(names = {"-p", "--page"}, defaultValue = "0", description = "Номер страницы")
    int page;

    @CommandLine.Option(names = {"-s", "--size"}, defaultValue = "20", description = "Размер страницы")
    int size;

    @Override
    public void run() {
        try {
            ListResponse<CreateEndpointResponseExtended> response = client.listEndpoints(page, size).await().indefinitely();

            if (response.getData() == null || response.getData().isEmpty()) {
                System.out.println("Эндпоинты не найдены.");
                return;
            }

            System.out.printf("%-36s | %-20s | %-12s | %-15s%n", "ENDPOINT ID", "NAME", "STATUS", "FLAVOR");
            System.out.println("-".repeat(90));

            for (var e : response.getData()) {
                System.out.printf("%-36s | %-20s | %-12s | %-15s%n",
                        e.endpointId(),
                        e.name(),
                        e.status(),
                        e.gpuFlavor() != null ? e.gpuFlavor() : "N/A");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении списка: " + e.getMessage());
        }
    }
}