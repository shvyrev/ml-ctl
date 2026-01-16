package io.cx.ml.cli.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

/**
 * DTO для хранения токенов аутентификации.
 */
@Setter
@Getter
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenData {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_at")
    private Long expiresAt; // timestamp в миллисекундах
}