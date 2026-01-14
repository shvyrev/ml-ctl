package io.cx.ml.cli.commands.endpoints;

import io.cx.ml.cli.clients.EndpointClient;
import io.cx.ml.cli.dto.EndpointCreateRequestExtended;
import io.cx.ml.cli.dto.GpuFlavor;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.util.UUID;

@CommandLine.Command(name = "create", description = "Создать эндпоинт и задеплоить модель")
public class EndpointCreateCommand implements Runnable {

    @Inject
    @RestClient
    EndpointClient client;

    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "Имя эндпоинта")
    String name;

    @CommandLine.Option(names = {"-m", "--model-id"}, required = true, description = "UUID модели")
    UUID modelId;

    @CommandLine.Option(names = {"-d", "--desc"}, description = "Описание эндпоинта")
    String description;

    @CommandLine.Option(names = {"-f", "--flavor"}, defaultValue = "COMMUNAL",
            description = "Тип ресурсов (GPU Flavor). По умолчанию: COMMUNAL")
    GpuFlavor flavor;

    @Override
    public void run() {
        var req = new EndpointCreateRequestExtended()
                .setName(name)
                .setModelId(modelId)
                .setDescription(description)
                .setFlavor(flavor);

        System.out.println("Создание эндпоинта для модели " + modelId + "...");

        try {
            var res = client.createEndpoint(req).await().indefinitely();
            System.out.println("✅ Запрос принят успешно!");
            System.out.println("ID Эндпоинта: " + res.endpointId());
            System.out.println("Текущий статус: " + res.status());
        } catch (jakarta.ws.rs.WebApplicationException e) {
            String errorBody = e.getResponse().readEntity(String.class);
            System.err.println("❌ Ошибка сервера: " + errorBody);
        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
        }
    }
}