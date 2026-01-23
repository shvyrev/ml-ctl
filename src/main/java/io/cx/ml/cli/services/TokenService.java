package io.cx.ml.cli.services;

import io.cx.ml.cli.clients.KeycloakAuthClient;
import io.cx.ml.cli.config.AppConfig;
import io.cx.ml.cli.config.AuthConfig;
import io.cx.ml.cli.dao.ConfigStore;
import io.cx.ml.cli.dto.TokenResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.Optional;

/**
 * Основной сервис для работы с токенами.
 * Объединяет работу с конфигурацией (диск) и Keycloak (сеть).
 */
@Slf4j
@ApplicationScoped
public class TokenService {

    @Inject
    ConfigStore configStore;

    /**
     * Возвращает живой Access Token.
     * Если токен просрочен — пытается обновить через Refresh Token.
     */
    public Optional<String> getValidAccessToken() {
        return configStore.load()
                .map(AppConfig::getAuthConfig)
                .flatMap(authConfig -> {
                    // Если токенов вообще нет — выходим
                    if (authConfig.noTokens()) {
                        return Optional.empty();
                    }

                    // Если просрочен — обновляем
                    if (authConfig.isExpired()) {
                        return refresh()
                                .map(TokenResponse::getAccessToken);
                    }

                    return Optional.ofNullable(authConfig.getAccessToken());
                });
    }

    /**
     * Выполняет вход по логину и паролю.
     */
    public Optional<TokenResponse> login(String user, String pass) {
        AppConfig config = configStore.load().orElseGet(this::createDefaultConfig);
        AuthConfig auth = config.getAuthConfig();

        try {
            String fullUrl = String.format("%s/realms/%s", auth.getUrl(), auth.getRealm());
            TokenResponse resp = getClient(fullUrl).fetchToken(
                    "password",
                    auth.getClientId(),
                    auth.getClientSecret(),
                    user,
                    pass,
                    null
            );

            updateAndSaveConfig(config, resp);
            return Optional.of(resp);
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Обновляет сессию, используя имеющийся Refresh Token.
     */
    public Optional<TokenResponse> refresh() {
        return configStore.load().flatMap(config -> {
            AuthConfig auth = config.getAuthConfig();
            if (!auth.hasRefreshToken()) {
                return Optional.empty();
            }

            try {
                String fullUrl = String.format("%s/realms/%s", auth.getUrl(), auth.getRealm());
                TokenResponse resp = getClient(fullUrl).fetchToken(
                        "refresh_token",
                        auth.getClientId(),
                        auth.getClientSecret(),
                        null,
                        null,
                        auth.getRefreshToken()
                );

                updateAndSaveConfig(config, resp);
                return Optional.of(resp);
            } catch (Exception e) {
                // Если refresh не удался (400 Bad Request), значит refresh_token тоже сдох.
                // Очищаем токены, чтобы пользователь точно знал, что нужно логиниться.
                log.error("Сессия истекла. Очистка старых токенов...");
                clearTokens();
                return Optional.empty();
            }
        });
    }
    /**
     * Создает REST клиент динамически, так как URL может меняться пользователем.
     */
    @SneakyThrows
    private KeycloakAuthClient getClient(String fullUrl) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create(fullUrl))
                .build(KeycloakAuthClient.class);
    }

    /**
     * Обновляет объект конфига данными из ответа Keycloak и пишет на диск.
     */
    private void updateAndSaveConfig(AppConfig config, TokenResponse resp) {
        AuthConfig auth = config.getAuthConfig();
        auth.setAccessToken(resp.getAccessToken());
        auth.setRefreshToken(resp.getRefreshToken());
        // Рассчитываем время истечения: текущий момент + секунды из ответа
        auth.setExpiresAt(System.currentTimeMillis() + (resp.getExpiresIn() * 1000));

        configStore.store(config);
    }

    private AppConfig createDefaultConfig() {
        return new AppConfig().setAuthConfig(
                new AuthConfig()
                        .setUrl("http://localhost:8082")
                        .setRealm("model-registry-realm")
                        .setClientId("model-registry-app")
                        .setClientSecret("LZbXY16jR0BRFazUKO3qTAoqXL3Uoet7")
        );
    }

    /**
     * Сброс токенов (Logout)
     */
    public void clearTokens() {
        configStore.load().ifPresent(config -> {
            config.getAuthConfig().setAccessToken(null);
            config.getAuthConfig().setRefreshToken(null);
            config.getAuthConfig().setExpiresAt(0);
            configStore.store(config);
        });
    }
}