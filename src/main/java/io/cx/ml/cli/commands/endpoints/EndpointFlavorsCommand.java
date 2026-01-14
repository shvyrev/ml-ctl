package io.cx.ml.cli.commands.endpoints;

import io.cx.ml.cli.clients.EndpointClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

@CommandLine.Command(name = "flavors", description = "Список доступных GPU конфигураций")
public class EndpointFlavorsCommand implements Runnable {

    @Inject
    @RestClient
    EndpointClient client;

    @Override
    public void run() {
        try {
            var flavors = client.getGpuFlavors().await().indefinitely();
            System.out.printf("%-15s | %-20s | %-10s%n", "VALUE", "MODEL", "RESOURCES");
            System.out.println("-".repeat(50));
            for (var f : flavors) {
                System.out.printf("%-15s | %-20s | %-10s%n", f.value(), f.model(), f.resources());
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}