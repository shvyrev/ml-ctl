package io.cx.ml.cli.services;

import io.cx.ml.cli.clients.KeycloakAuthClient;
import io.cx.ml.cli.config.AppConfig;
import io.cx.ml.cli.config.AuthConfig;
import io.cx.ml.cli.dao.ConfigStore;
import io.cx.ml.cli.dto.TokenResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.Optional;

import static java.util.function.Predicate.not;

@ApplicationScoped
public class TokenManager {

    @Inject
    ConfigStore configStore;

    public Optional<TokenResponse> login(String user, String pass) {
        // 1. Грузим конфиг или создаем дефолтный, если файла нет
        AppConfig config = configStore.load().orElseGet(this::createDefaultConfig);
        AuthConfig authConfig = config.getAuthConfig();

        try {
            // 2. Идем в Keycloak
            TokenResponse tokenResponse = login(
                    authConfig.getUrl(),
                    authConfig.getRealm(),
                    authConfig.getClientId(),
                    authConfig.getClientSecret(),
                    user,
                    pass
            );

            // 3. ВАЖНО: Сохраняем полученные токены в конфиг
            authConfig.setAccessToken(tokenResponse.getAccessToken());
            authConfig.setRefreshToken(tokenResponse.getRefreshToken());
            // expires_in приходит в секундах, переводим в timestamp
            authConfig.setExpiresAt(System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000));

            // 4. Пишем на диск
            configStore.store(config);

            return Optional.of(tokenResponse);
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Дефолтные настройки (из твоего EntryCommand)
    private AppConfig createDefaultConfig() {
        return new AppConfig().setAuthConfig(
                new AuthConfig()
                        .setUrl("http://localhost:8082")
                        .setRealm("model-registry-realm")
                        .setClientId("model-registry-app")
                        .setClientSecret("LZbXY16jR0BRFazUKO3qTAoqXL3Uoet7")
        );
    }

    public Optional<TokenResponse> refresh() {
        return configStore.load()
                .map(AppConfig::getAuthConfig)
                .map(authConfig -> {
                    TokenResponse resp = refresh(
                            authConfig.getUrl(),
                            authConfig.getRealm(),
                            authConfig.getClientId(),
                            authConfig.getClientSecret(),
                            authConfig.getRefreshToken());

                    // Обновляем и сохраняем
                    authConfig.setAccessToken(resp.getAccessToken());
                    authConfig.setRefreshToken(resp.getRefreshToken());
                    authConfig.setExpiresAt(System.currentTimeMillis() + (resp.getExpiresIn() * 1000));
                    configStore.store(new AppConfig().setAuthConfig(authConfig));

                    return resp;
                });
    }

    // Низкоуровневые методы оставляем как есть, они нормальные
    public TokenResponse login(String baseUrl, String realm, String clientId, String secret, String user, String pass) {
        String fullUrl = String.format("%s/realms/%s", baseUrl, realm);
        return getClient(fullUrl).fetchToken("password", clientId, secret, user, pass, null);
    }

    public TokenResponse refresh(String baseUrl, String realm, String clientId, String secret, String refreshToken) {
        String fullUrl = String.format("%s/realms/%s", baseUrl, realm);
        return getClient(fullUrl).fetchToken("refresh_token", clientId, secret, null, null, refreshToken);
    }

    @SneakyThrows
    private KeycloakAuthClient getClient(String fullUrl) {
        return RestClientBuilder.newBuilder()
                .baseUrl(URI.create(fullUrl).toURL())
                .build(KeycloakAuthClient.class);
    }

    public Optional<String> getValidAccessToken() {
        return configStore.load()
                .map(AppConfig::getAuthConfig)
                .map(authConfig -> authConfig.isExpired()
                        ? refresh().map(TokenResponse::getAccessToken).orElse(null)
                        : authConfig.getAccessToken());
    }
}