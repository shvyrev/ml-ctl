package io.cx.ml.cli.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@RegisterForReflection
public class AuthConfig {
    private String url;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;
    private long expiresAt;

    @JsonIgnore
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }

    @JsonIgnore
    public boolean hasRefreshToken() {
        return ofNullable(refreshToken)
                .filter(not(String::isBlank))
                .isPresent();
    }

    @JsonIgnore
    public boolean hasAccessToken() {
        return ofNullable(accessToken)
                .filter(not(String::isBlank))
                .isPresent();
    }

    @JsonIgnore
    public boolean noTokens() {
        return Stream.of(accessToken, refreshToken)
                .noneMatch(Objects::nonNull);
    }
}
