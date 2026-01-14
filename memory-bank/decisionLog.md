# Decision Log

This file records architectural and implementation decisions using a list format.
2026-01-05 11:30:00 - Log of updates made.

*

## Decision

Использовать Quarkus (версия 3.x) как основу приложения с расширениями:
- `quarkus-picocli` для CLI функциональности.
- `quarkus-rest-client-reactive-jackson` для REST клиента.
- `quarkus-oidc-client` для получения токенов от Keycloak.
- `quarkus-jackson` для JSON сериализации.
- `quarkus-arc` для CDI.
- `picocli` для парсинга команд.

## Rationale

Quarkus обеспечивает быстрый старт, интеграцию с GraalVM для native image, реактивный стек и удобное управление зависимостями через расширения. Picocli хорошо интегрируется с Quarkus и предоставляет аннотации для определения команд. REST Client Reactive — современный реактивный HTTP клиент, поддерживающий JSON через Jackson. OIDC Client упрощает взаимодействие с Keycloak для OAuth2.

## Implementation Details

* Версия Java: 17.
* Сборка через Maven.
* Структура проекта: стандартная Maven структура с `src/main/java`, `src/main/resources`.
* Конфигурация в `application.properties` с префиксами `quarkus.oidc.*`, `quarkus.rest-client.*`.
* Токены будут храниться в файле `~/.cx/config` в формате JSON.

## Decision

Использовать container-build для создания native image (через Docker) с целью обеспечения совместимости с Linux-окружением, даже при разработке на macOS. Это упрощает сборку, так как не требует установки GraalVM локально, но создаёт бинарник для Linux (ARM aarch64), который не исполняется напрямую на macOS без эмуляции.

## Rationale

Container-build гарантирует воспроизводимость и устраняет зависимость от специфичной версии GraalVM на хосте. Однако для конечного использования на macOS потребуется либо кросс-компиляция, либо локальная сборка с `-Dquarkus.native.container-build=false`. Для демонстрации работы CLI достаточно JAR-файла, который уже протестирован.

## Implementation Details

* Сборка выполнена командой `mvn package -Pnative -DskipTests -Dquarkus.native.container-build=true`.
* Размер полученного бинарника ~58 MB.
* Бинарник динамически линкован с glibc (интерпретатор `/lib/ld-linux-aarch64.so.1`).