package io.cx.ml.cli.commands;

import io.cx.ml.cli.config.AppConfig;
import io.cx.ml.cli.dao.ConfigStore;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "init",
        description = "Настройка базовых адресов API и параметров подключения")
public class InitCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--serving"}, description = "URL для Serving API (модели)")
    private String servingUrl;

    @CommandLine.Option(names = {"--artifact"}, description = "URL для Artifact API (хранилище)")
    private String artifactUrl;

    @Inject
    ConfigStore configStore;

    @Override
    public Integer call() {
        // Загружаем текущий конфиг или создаем новый
        AppConfig config = configStore.load().orElseGet(AppConfig::new);

        if (servingUrl != null) config.setServingApiUrl(servingUrl);
        if (artifactUrl != null) config.setArtifactApiUrl(artifactUrl);

        configStore.save(config);

        System.out.println("✅ Конфигурация успешно сохранена в " + configStore.getConfigPath());
        System.out.println("Serving API: " + config.getServingApiUrl());
        System.out.println("Artifact API: " + config.getArtifactApiUrl());
        System.out.println("\nДля применения изменений может потребоваться перезапуск CLI.");

        return 0;
    }
}