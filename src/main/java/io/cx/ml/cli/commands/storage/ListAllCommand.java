package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.clients.ArtifactStoreClient;
import io.cx.ml.cli.dto.UserAllContentResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "tree", description = "Показать весь контент пользователя (весь список)")
public class ListAllCommand implements Callable<Integer> {

    @Option(names = {"-u", "--user"}, description = "Идентификатор пользователя", required = true)
    private String userId;

    @Inject
    @RestClient
    ArtifactStoreClient artifactStoreClient;

    @Override
    public Integer call() {
        try {
            System.out.printf("Запрос контента для пользователя: %s...%n", userId);

            // Передаем userId в клиент
            UserAllContentResponse response = artifactStoreClient.getUserContent(userId)
                    .await().indefinitely();

            System.out.println("\n--- ПАПКИ ---");
            if (response.getFolders() == null || response.getFolders().isEmpty()) {
                System.out.println(" (пусто)");
            } else {
                response.getFolders().forEach(f ->
                        System.out.printf("  [DIR]  ID: %-5d | %s%n", f.getId(), f.getName()));
            }

            System.out.println("\n--- ФАЙЛЫ ---");
            if (response.getFiles() == null || response.getFiles().isEmpty()) {
                System.out.println(" (пусто)");
            } else {
                System.out.printf("  %-30s | %-15s | %s%n", "NAME", "SIZE", "PATH");
                System.out.println("  ------------------------------------------------------------------");
                response.getFiles().forEach(f ->
                        System.out.printf("  [FILE] %-30s | %-15d | %s%n",
                                truncate(f.getName(), 30),
                                f.getSize(),
                                f.getPath()));
            }

            return 0;
            // Пример для ListAllCommand / PurgeCommand
        } catch (Exception e) {
            if (e.getCause() instanceof jakarta.ws.rs.WebApplicationException webEx) {
                Response response = webEx.getResponse();
                // Пытаемся прочитать тело как строку, чтобы увидеть JSON от маппера
                String errorBody = response.readEntity(String.class);
                System.err.println("ОШИБКА СЕРВЕРА (" + response.getStatus() + "): " + errorBody);
            } else if (e instanceof jakarta.ws.rs.WebApplicationException webEx) {
                String errorBody = webEx.getResponse().readEntity(String.class);
                System.err.println("ОШИБКА СЕРВЕРА (" + webEx.getResponse().getStatus() + "): " + errorBody);
            } else {
                System.err.println("ЛОКАЛЬНАЯ ОШИБКА: " + e.getMessage());
            }
            return 1;
        }
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }
}