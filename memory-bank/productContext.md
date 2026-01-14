# Product Context

This file provides a high-level overview of the project and the expected product that will be created. Initially it is based upon projectBrief.md (if provided) and all other available project-related information in the working directory. This file is intended to be updated as the project evolves, and should be used to inform all other modes of the project's goals and context.
2026-01-05 11:26:50 - Log of updates made will be appended as footnotes to the end of this file.

*

## Project Goal

* Создать CLI-приложение "cx-ml-cli" для взаимодействия с удаленным REST API, аналогичное AWS CLI, с аутентификацией через Keycloak (OIDC) и возможностью сборки в Native Image с помощью Quarkus и GraalVM.

## Key Features

* Команда `login` для интерактивного ввода учетных данных и получения Access/Refresh токенов от Keycloak.
* Команда `ls` для вывода содержимого bucket'а (или списка папок) через REST API.
* Автоматическое управление токенами: обновление Access Token с помощью Refresh Token при истечении срока действия.
* Сохранение токенов в локальном файле конфигурации `~/.cx/config`.
* REST Client с фильтром запросов, автоматически добавляющим заголовок Authorization.
* Поддержка пагинации в API.
* Полная совместимость с Native Image (аннотации `@RegisterForReflection`).

## Overall Architecture

* **Фреймворк**: Quarkus (последняя стабильная версия) с расширениями: picocli, rest-client-reactive-jackson, oidc-client.
* **CLI парсер**: Picocli для определения команд и параметров.
* **HTTP клиент**: Quarkus REST Client Reactive для вызовов API.
* **Аутентификация**: TokenService для чтения/записи токенов, обновления через Keycloak.
* **Интерцептор**: ClientRequestFilter (или `@ClientHeaderParam`) для автоматической подстановки Bearer токена.
* **Сериализация**: Jackson для JSON.
* **Сборка**: Maven, профиль `native` для создания native image.