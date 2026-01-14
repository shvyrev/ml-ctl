package io.cx.ml.cli.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.ToString;

/**
 * DTO для хранения токенов аутентификации.
 */
@RegisterForReflection
public class TokenData {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_at")
    private Long expiresAt; // timestamp в миллисекундах

    // Конструкторы
    public TokenData() {}

    public TokenData(String accessToken, String refreshToken, Long expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    // Геттеры и сеттеры
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "TokenData{" +
                "accessToken='" + (accessToken != null ? "***" : null) + '\'' +
                ", refreshToken='" + (refreshToken != null ? "***" : null) + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}