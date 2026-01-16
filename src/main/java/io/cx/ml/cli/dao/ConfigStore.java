package io.cx.ml.cli.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.cx.ml.cli.config.AppConfig;
import io.cx.ml.cli.config.AuthConfig;
import io.cx.ml.cli.exceptions.NeedAuthException;
import io.cx.ml.cli.exceptions.NeedRefreshException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
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

    public AppConfig getAppConfig() {
        return load().orElseGet(() -> new AppConfig().setAuthConfig(new AuthConfig()));
    }

    public String resolveAccessToken() {
        AppConfig config = load().orElseThrow(NeedAuthException::new);
        AuthConfig auth = config.getAuthConfig();

        if (auth == null || auth.noTokens()) throw new NeedAuthException();

        if (auth.isExpired()) {
            if (auth.hasRefreshToken()) throw new NeedRefreshException();
            throw new NeedAuthException();
        }

        return auth.getAccessToken();
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
    public void save(AppConfig config) {
        Path path = getConfigPath();
        Files.createDirectories(path.getParent());
        json.writeValue(path.toFile(), config);
        log.info("Config saved to {}", path);
    }

    public void updateAuth(AuthConfig auth) {
        AppConfig current = getAppConfig();
        current.setAuthConfig(auth);
        save(current);
    }

    public String getAccessTokenOrThrow() {
        AppConfig config = getAppConfig();
        AuthConfig auth = config.getAuthConfig();

        if (auth == null || auth.noTokens()) {
            throw new NeedAuthException();
        }

        if (auth.isExpired()) {
            if (auth.hasRefreshToken()) {
                log.warn("Token expired, refresh required");
                throw new NeedRefreshException();
            } else {
                throw new NeedAuthException();
            }
        }

        return auth.getAccessToken();
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
        Path path = getConfigPath();
        if (!Files.exists(path)) return Optional.empty();

        try {
            return Optional.ofNullable(json.readValue(path.toFile(), AppConfig.class));
        } catch (IOException e) {
            log.error("Failed to read config: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void store(AuthConfig authConfig) {
        AppConfig appConfig = new AppConfig()
                .setAuthConfig(authConfig);
        store(appConfig);
    }
}
