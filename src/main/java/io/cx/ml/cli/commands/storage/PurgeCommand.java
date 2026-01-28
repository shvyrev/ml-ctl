package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.clients.ArtifactStoreClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "purge", description = "УДАЛИТЬ ВЕСЬ КОНТЕНТ ПОЛЬЗОВАТЕЛЯ (MinIO + DB)")
public class PurgeCommand implements Callable<Integer> {

    @Option(names = {"-f", "--force"}, description = "Подтверждение удаления без лишних вопросов")
    private boolean force;

    @Option(names = {"-u", "--user"}, description = "Идентификатор пользователя", required = true)
    private String userId;

    @Inject
    @RestClient
    ArtifactStoreClient artifactStoreClient;

    @Override
    public Integer call() {
        // Проверка флага принудительного удаления
        if (!force) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("ВНИМАНИЕ: Данная команда УДАЛИТ ВЕСЬ контент пользователя: " + userId);
            System.err.println("Это действие необратимо (MinIO + PostgreSQL).");
            System.err.println("Используйте флаг -f для подтверждения.");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return 1;
        }

        try {
            System.out.printf("Начинаю полную очистку контента для пользователя [%s]... ", userId);

            // Вызываем метод с передачей userId
            artifactStoreClient.deleteUserContent(userId).await().indefinitely();

            System.out.println("УСПЕШНО.");
            return 0;
        } catch (Exception e) {
            System.err.println("\nОШИБКА при удалении: " + e.getMessage());
            // Если ошибка в 404, возможно пользователя уже нет или клиент криво настроен
            return 1;
        }
    }
}