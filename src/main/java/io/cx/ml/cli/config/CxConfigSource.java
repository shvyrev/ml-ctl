package io.cx.ml.cli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.spi.ConfigSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CxConfigSource implements ConfigSource {

    private final Map<String, String> properties = new HashMap<>();

    public CxConfigSource() {
        fetchConfig();
    }

    private void fetchConfig() {
        Path path = Paths.get(System.getProperty("user.home"), ".cx", "config");
        if (Files.exists(path)) {
            try {
                // Используем "сырой" маппер, так как CDI еще может быть не готов
                ObjectMapper mapper = new ObjectMapper();
                AppConfig config = mapper.readValue(path.toFile(), AppConfig.class);

                if (config.getServingApiUrl() != null) {
                    properties.put("quarkus.rest-client.\"serving-api\".url", config.getServingApiUrl());
                }
                if (config.getArtifactApiUrl() != null) {
                    properties.put("quarkus.rest-client.\"artifact-api\".url", config.getArtifactApiUrl());
                }
            } catch (Exception e) {
                // В лог писать рано, просто игнорируем ошибки парсинга при старте
            }
        }
    }

    @Override
    public Map<String, String> getProperties() { return properties; }

    @Override
    public Set<String> getPropertyNames() { return properties.keySet(); }

    @Override
    public String getValue(String propertyName) { return properties.get(propertyName); }

    @Override
    public String getName() { return "CxFileConfigSource"; }

    @Override
    public int getOrdinal() { return 300; } // Выше чем application.properties (100)
}