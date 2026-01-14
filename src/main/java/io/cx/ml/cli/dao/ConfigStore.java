package io.cx.ml.cli.dao;

import io.cx.ml.cli.config.AppConfig;
import io.cx.ml.cli.config.AuthConfig;
import io.cx.ml.cli.exceptions.NeedAuthException;
import io.cx.ml.cli.exceptions.NeedRefreshException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Log
@ApplicationScoped
public class ConfigStore {

    private static final String CONFIG_DIR = ".cx";
    private static final String CONFIG_FILE = "config";
    String userHome = System.getProperty("user.home");

    private final ObjectMapper json = new JsonMapper()
            .findAndRegisterModules();

    public Path getConfigPath() {
        return Paths.get(userHome, CONFIG_DIR, CONFIG_FILE);
    }

    public String getAccessToken() {
        try {
            return load()
                    .map(s -> {
                        log.info("$ "+ "load " + s);
                        return s;
                    })
                    .map(AppConfig::getAuthConfig)
                    .map(authConfig -> {
                        log.info("$ "+ "getAccessToken() called");

                        if (authConfig.noTokens()) {
                            log.info("$ "+ "no tokens");
                            throw new NeedAuthException();
                         }

                        if(authConfig.isExpired() && authConfig.hasRefreshToken()) {
                            log.info("$ "+ "refresh token");
                            throw new NeedRefreshException();
                        }
                        return authConfig.getAccessToken();
                    })
                    .orElseThrow(NeedAuthException::new);
        } catch (NeedRefreshException e) {
            log.info("$ "+ "NeedRefreshException");
            return "";
        }
    }

    @SneakyThrows
    @JsonIgnore
    public AppConfig store(AppConfig config) {
        log.info("$ "+ "store() called");
        Path configPath = getConfigPath();
        Files.createDirectories(configPath.getParent());
        Files.write(configPath, json.writeValueAsBytes(config));
        return config;
    }

    public Optional<AppConfig> load() {

        return ofNullable(getConfigPath())
                .filter(Files::exists)
                .map(Path::toFile)
                .map(f -> {
//                    TODO: добавить rethrow
                    try {
                        return json.readValue(f, AppConfig.class);
                    } catch (IOException e) {
                        return null;
                    }
                });
    }

    public void store(AuthConfig authConfig) {
        AppConfig appConfig = new AppConfig()
                .setAuthConfig(authConfig);
        store(appConfig);
    }
}
