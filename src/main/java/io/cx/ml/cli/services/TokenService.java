package io.cx.ml.cli.services;

import io.cx.ml.cli.auth.TokenData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Сервис для управления токенами аутентификации.
 * Сохраняет и загружает токены из файла конфигурации в домашней директории.
 */
@ApplicationScoped
public class TokenService {

    private static final String CONFIG_DIR = ".cx";
    private static final String CONFIG_FILE = "config";

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "user.home")
    String userHome;

    private Path getConfigPath() {
        return Paths.get(userHome, CONFIG_DIR, CONFIG_FILE);
    }

    /**
     * Загружает токены из файла конфигурации.
     * @return TokenData или null, если файл не существует или некорректен.
     */
    public TokenData loadTokens() {
        Path path = getConfigPath();
        if (!Files.exists(path)) {
            return null;
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            return objectMapper.readValue(bytes, TokenData.class);
        } catch (IOException e) {
            // Логирование ошибки
            System.err.println("Failed to load tokens from " + path + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Сохраняет токены в файл конфигурации.
     * @param tokenData данные токенов
     */
    public void saveTokens(TokenData tokenData) {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            byte[] bytes = objectMapper.writeValueAsBytes(tokenData);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save tokens to " + path, e);
        }
    }

    /**
     * Проверяет, истек ли срок действия access токена.
     * @param tokenData данные токенов
     * @return true если токен просрочен или отсутствует
     */
    public boolean isTokenExpired(TokenData tokenData) {
        if (tokenData == null || tokenData.getAccessToken() == null) {
            return true;
        }
        Long expiresAt = tokenData.getExpiresAt();
        if (expiresAt == null) {
            return true;
        }
        return System.currentTimeMillis() >= expiresAt;
    }

    /**
     * Обновляет токены с использованием refresh токена.
     * @param oldToken текущие токены
     * @return новые токены
     * @throws IOException если обновление не удалось
     */
    public TokenData refreshTokens(TokenData oldToken) throws IOException {
        if (oldToken.getRefreshToken() == null) {
            throw new IOException("No refresh token available");
        }
        // Заглушка: в реальности нужно вызвать Keycloak endpoint
        // Для простоты выбросим исключение, требующее повторного логина
        throw new IOException("Token refresh not implemented. Please login again.");
    }

    /**
     * Возвращает валидный access токен, при необходимости обновляя его.
     * @return access токен
     * @throws RuntimeException если токены отсутствуют или не могут быть обновлены
     */
    public Optional<String> getValidAccessToken() {
        TokenData tokenData = loadTokens();
        if (tokenData == null) {
            return Optional.empty();
        }
        if (isTokenExpired(tokenData)) {
            try {
                tokenData = refreshTokens(tokenData);
            } catch (IOException e) {
                // Если refresh не удался, возвращаем пустой Optional
                return Optional.empty();
            }
        }
        return Optional.of(tokenData.getAccessToken());
    }

    /**
     * Удаляет сохраненные токены (логаут).
     */
    public void clearTokens() {
        Path path = getConfigPath();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // игнорируем
        }
    }
}