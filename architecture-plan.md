# Архитектурный план: cx-ml-cli

## Структура проекта

```
cx-ml-cli/
├── pom.xml
├── src/main/java/com/cloudx/cxmlcli/
│   ├── Main.java
│   ├── cli/
│   │   ├── EntryCommand.java
│   │   ├── LoginCommand.java
│   │   └── LsCommand.java
│   ├── auth/
│   │   ├── TokenService.java
│   │   ├── TokenData.java
│   │   └── AuthFilter.java
│   ├── api/
│   │   ├── client/
│   │   │   ├── ArtifactStoreClient.java
│   │   │   └── PaginatedResponse.java
│   │   └── dto/
│   │       ├── FolderItem.java
│   │       └── Paging.java
│   └── config/
│       └── ConfigManager.java
├── src/main/resources/
│   └── application.properties
└── README.md
```

## Описание файлов

### 1. pom.xml

Основные зависимости:

- `quarkus-bom` (управление версиями)
- `quarkus-picocli`
- `quarkus-rest-client-reactive-jackson`
- `quarkus-oidc-client`
- `quarkus-jackson`
- `picocli`
- `quarkus-arc`
- `jackson-databind`
- `quarkus-junit5` (для тестов)

Плагины:
- `quarkus-maven-plugin` для сборки native image.

### 2. application.properties

```properties
# Keycloak OIDC конфигурация
quarkus.oidc.client-id=cx-ml-cli
quarkus.oidc.auth-server-url=${KEYCLOAK_URL:http://localhost:8080/auth/realms/master}
quarkus.oidc.credentials.secret=${CLIENT_SECRET:secret}

# REST клиент
quarkus.rest-client.artifact-store-api.url=${API_BASE_URL:http://artifact-store.local}
quarkus.rest-client.artifact-store-api.scope=javax.inject.Singleton

# Конфигурация CLI
quarkus.picocli.program-name=cx-ml-cli
```

### 3. Основные классы

#### Main.java
Точка входа Quarkus Picocli приложения.

#### EntryCommand.java
Корневая команда с подкомандами `login` и `ls`.

#### LoginCommand.java
Запрашивает username/password, получает токены через OIDC клиент, сохраняет в TokenService.

#### LsCommand.java
Принимает аргумент bucket-name, вызывает REST клиент для получения списка папок, выводит результат.

#### TokenService.java
Управление токенами: чтение/запись файла `~/.cx/config`, проверка срока действия, обновление через Refresh Token.

#### TokenData.java
DTO для хранения access_token, refresh_token, expires_at.

#### AuthFilter.java
`ClientRequestFilter`, который добавляет заголовок `Authorization: Bearer <token>` к исходящим запросам REST клиента.

#### ArtifactStoreClient.java
REST Client интерфейс с методами для вызова API.

#### PaginatedResponse.java
Общий класс для ответов с пагинацией.

#### FolderItem.java, Paging.java
DTO для данных.

#### ConfigManager.java
Управление конфигурацией (загрузка свойств).

## Последовательность работы

1. Пользователь запускает `cx-ml-cli login` → ввод учетных данных → получение токенов → сохранение.
2. При вызове любой команды, требующей аутентификации, AuthFilter автоматически добавляет токен.
3. Если токен просрочен, TokenService обновляет его с помощью refresh token.
4. Команда `ls` выполняет GET запрос к `/api/v1/folders/` с параметрами пагинации.

## Native Image совместимость

- Все DTO должны быть аннотированы `@RegisterForReflection`.
- Использовать `@ResourceHint` для ресурсов конфигурации.
- Избегать динамического reflection.

## Следующие шаги

1. Создать pom.xml с зависимостями.
2. Написать TokenService и AuthFilter.
3. Реализовать команды Picocli.
4. Настроить REST клиент.
5. Протестировать сборку native image.

## Детальное проектирование TokenService

### TokenData.java
```java
@RegisterForReflection
public class TokenData {
    private String accessToken;
    private String refreshToken;
    private Long expiresAt; // timestamp в миллисекундах

    // геттеры/сеттеры
}
```

### TokenService.java
```java
@ApplicationScoped
public class TokenService {
    private static final String CONFIG_PATH = System.getProperty("user.home") + "/.cx/config";

    public TokenData loadTokens() {
        // чтение JSON из файла, преобразование в TokenData
    }

    public void saveTokens(TokenData tokenData) {
        // запись TokenData в файл в формате JSON
    }

    public boolean isTokenExpired(TokenData tokenData) {
        return tokenData.getExpiresAt() == null ||
               System.currentTimeMillis() >= tokenData.getExpiresAt();
    }

    public TokenData refreshTokens(TokenData oldToken) throws IOException {
        // HTTP POST к Keycloak с refresh_token для получения новой пары
        // использует quarkus-oidc-client или REST клиент
        // возвращает новый TokenData
    }

    public Optional<String> getValidAccessToken() {
        // загружает токены, проверяет срок, при необходимости обновляет,
        // сохраняет новые токены, возвращает accessToken
    }
}
```

### AuthFilter.java
```java
@Provider
@ClientHeaderParam(name = "Authorization", value = "{getAuthHeader}")
@RegisterForReflection
public class AuthFilter implements ClientRequestFilter {
    @Inject
    TokenService tokenService;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        // автоматически вызывается перед отправкой запроса
        // можно добавить логику, но @ClientHeaderParam уже добавляет заголовок
    }

    public String getAuthHeader() {
        return tokenService.getValidAccessToken()
                .map(token -> "Bearer " + token)
                .orElseThrow(() -> new RuntimeException("Not authenticated. Please login."));
    }
}
```

### Конфигурация OIDC клиента
В `application.properties`:
```properties
quarkus.oidc.client-id=cx-ml-cli
quarkus.oidc.auth-server-url=${KEYCLOAK_URL}
quarkus.oidc.credentials.secret=${CLIENT_SECRET}
quarkus.oidc.token-path=/protocol/openid-connect/token
```

Для получения токена по паролю потребуется использовать `OidcClient` из `quarkus-oidc-client`.

### LoginCommand.java (фрагмент)
```java
@Command(name = "login", description = "Authenticate with Keycloak")
public class LoginCommand implements Runnable {
    @Inject
    OidcClient oidcClient;
    @Inject
    TokenService tokenService;

    @Override
    public void run() {
        Console console = System.console();
        String username = console.readLine("Username: ");
        char[] password = console.readPassword("Password: ");
        
        // запрос токена через OidcClient
        // сохранение через tokenService.saveTokens(...)
    }
}
```

## Детальное проектирование Picocli команд

### Main.java
```java
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;

@QuarkusMain
public class Main implements QuarkusApplication {
    @Override
    public int run(String... args) {
        return new CommandLine(new EntryCommand()).execute(args);
    }
}
```

### EntryCommand.java
```java
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "cx-ml-cli",
        description = "CLI for interacting with the ML artifact store",
        subcommands = {LoginCommand.class, LsCommand.class},
        mixinStandardHelpOptions = true)
public class EntryCommand implements Runnable {
    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        // Если не указана подкоманда, выводим справку
        spec.commandLine().usage(System.err);
    }
}
```

### LoginCommand.java
```java
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.Console;

@Command(name = "login", description = "Authenticate with Keycloak")
public class LoginCommand implements Runnable {
    @Inject
    OidcClient oidcClient;
    @Inject
    TokenService tokenService;

    @Option(names = {"-u", "--username"}, description = "Username")
    String username;

    @Option(names = {"-p", "--password"}, description = "Password", interactive = true)
    char[] password;

    @Override
    public void run() {
        Console console = System.console();
        if (username == null) {
            username = console.readLine("Username: ");
        }
        if (password == null) {
            password = console.readPassword("Password: ");
        }

        try {
            Tokens tokens = oidcClient.getTokens(username, new String(password)).await().indefinitely();
            TokenData tokenData = new TokenData();
            tokenData.setAccessToken(tokens.getAccessToken());
            tokenData.setRefreshToken(tokens.getRefreshToken());
            tokenData.setExpiresAt(System.currentTimeMillis() + tokens.getExpiresIn() * 1000);
            tokenService.saveTokens(tokenData);
            System.out.println("Login successful.");
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
        }
    }
}
```

### LsCommand.java
```java
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import javax.inject.Inject;

@Command(name = "ls", description = "List contents of a bucket")
public class LsCommand implements Runnable {
    @Inject
    ArtifactStoreClient client;

    @Parameters(index = "0", description = "Bucket name")
    String bucketName;

    @Option(names = {"--limit"}, description = "Pagination limit")
    Integer limit = 50;

    @Option(names = {"--offset"}, description = "Pagination offset")
    Integer offset = 0;

    @Override
    public void run() {
        try {
            PaginatedResponse<FolderItem> response = client.listFolders(bucketName, limit, offset);
            // Вывод в JSON или таблицу
            System.out.println(response.getData().size() + " items found");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

### Примечания
- Команды являются CDI бинами (используют `@Inject`).
- Picocli интегрируется с Quarkus через расширение `quarkus-picocli`.
- Для интерактивного ввода пароля используется `interactive = true`.
- Обработка ошибок и вывод сообщений.

## Детальное проектирование REST клиента и DTO

### ArtifactStoreClient.java
```java
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@RegisterRestClient(configKey = "artifact-store-api")
@RegisterProvider(AuthFilter.class)
@RegisterClientHeaders
public interface ArtifactStoreClient {
    @GET
    @Path("/api/v1/folders/")
    PaginatedResponse<FolderItem> listFolders(
            @QueryParam("bucket") String bucket,
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset
    );

    // Дополнительные методы по необходимости
}
```

### PaginatedResponse.java
```java
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class PaginatedResponse<T> {
    @JsonProperty("data")
    private List<T> data;
    @JsonProperty("paging")
    private Paging paging;

    // геттеры/сеттеры
}
```

### FolderItem.java
```java
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class FolderItem {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("path")
    private String path;
    @JsonProperty("size")
    private Long size;
    // другие поля по необходимости

    // геттеры/сеттеры
}
```

### Paging.java
```java
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Paging {
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("limit")
    private Integer limit;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("next")
    private String next;
    @JsonProperty("prev")
    private String prev;

    // геттеры/сеттеры
}
```

### Конфигурация REST клиента
В `application.properties`:
```properties
quarkus.rest-client.artifact-store-api.url=${API_BASE_URL:http://artifact-store.local}
quarkus.rest-client.artifact-store-api.scope=javax.inject.Singleton
```

### Использование в командах
Клиент инжектируется с помощью `@Inject` или `@RestClient`. Фильтр `AuthFilter` автоматически добавляет заголовок авторизации.

### Обработка ошибок
- Ошибки аутентификации (401) должны приводить к предложению выполнить `login`.
- Ошибки сети и таймауты должны логироваться.

### Тестирование REST клиента
Использовать `@QuarkusTest` и `@RestClient` моки.

## Native Image совместимость (детали)

### Аннотации Reflection
Все классы, которые сериализуются/десериализуются Jackson или используются в runtime через reflection, должны быть аннотированы `@RegisterForReflection`. Это включает:
- `TokenData`
- `PaginatedResponse`, `FolderItem`, `Paging`
- `LoginCommand`, `LsCommand` (если они сериализуются)
- Любые DTO, возвращаемые REST клиентом.

### Конфигурация GraalVM
В `src/main/resources/application.properties` можно добавить настройки native image:
```properties
quarkus.native.enable-vm-inspection=false
quarkus.native.additional-build-args=--allow-incomplete-classpath
```

### Ресурсы
Файл конфигурации `~/.cx/config` должен быть доступен во время выполнения. Поскольку это пользовательский файл, создаваемый во время выполнения, проблем не возникает.

### Использование динамического reflection
Избегать:
- `Class.forName()`
- Рефлексивный доступ к методам через `Method.invoke()` (если не аннотировано).
- Использование библиотек, которые полагаются на reflection (например, некоторые маперы). Jackson с `quarkus-jackson` работает корректно.

### Проверка сборки native image
Сборка выполняется командой:
```bash
mvn clean package -Pnative
```
Или с использованием Docker:
```bash
mvn clean package -Pnative -Dquarkus.native.container-build=true
```

### Тестирование native image
После сборки можно протестировать бинарный файл:
```bash
./target/cx-ml-cli-1.0.0-SNAPSHOT-runner --help
```

### Известные проблемы и решения
1. **OIDC клиент**: Убедиться, что используется расширение `quarkus-oidc-client`, которое совместимо с native image.
2. **HTTP клиент**: REST Client Reactive работает в native.
3. **Picocli**: Команды должны быть зарегистрированы в CDI.

### Рекомендации
- Использовать `quarkus-container-image` для создания Docker образа.
- Добавить `@ResourceHint` для ресурсов, если необходимо.

---
*План создан: 2026-01-05 11:32:47 UTC*