package io.cx.ml.cli.commands.storage;

import io.cx.ml.cli.services.TestService;
import io.cx.ml.cli.services.UploadService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "mup", description = "Рекурсивная загрузка папки")
public class MupCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "Локальный путь к папке")
    private String localPath;

    @CommandLine.Option(names = {"-p", "--path"}, description = "Базовый путь в облаке")
    private String cloudPath;

    @Inject
    UploadService uploadService;

    @Inject
    TestService testService;

    @Override
    public Integer call() {

        try {
            Path root = Path.of(localPath).toAbsolutePath();

            try (var stream = Files.walk(root)) {
                stream.filter(Files::isRegularFile).forEach(file -> {
                    // Вычисляем относительный путь для сохранения структуры
                    Path relative = root.relativize(file.getParent());
                    String targetCloudSubPath = (cloudPath == null ? "" : cloudPath) + "/" + relative;
                    targetCloudSubPath = targetCloudSubPath.replace("\\", "/");

                    try {
                        // Загружаем файл, передавая путь для резолвинга
                        uploadService.uploadFile(file, null, targetCloudSubPath);
                    } catch (Exception e) {
                        System.err.println("Ошибка при загрузке " + file + ": " + e.getMessage());
                    }
                });
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

    }
}
