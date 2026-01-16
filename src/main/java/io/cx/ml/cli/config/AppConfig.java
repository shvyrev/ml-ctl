package io.cx.ml.cli.config;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Log
@ToString
@RegisterForReflection
public class AppConfig {
    private AuthConfig authConfig;
    private String servingApiUrl = "http://localhost:8081";
    private String artifactApiUrl = "http://localhost:8083";
}
