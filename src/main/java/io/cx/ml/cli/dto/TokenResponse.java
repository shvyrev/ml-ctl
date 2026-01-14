package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@RegisterForReflection
public class TokenResponse {
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("refresh_token")
    public String refreshToken;

    @JsonProperty("expires_in")
    public long expiresIn;

    @JsonProperty("refresh_expires_in")
    public long refreshExpiresIn;
}