# Проект с примерами кода по предмету "Технологии программирования информационных систем"

java-backend-course-demo = jbcd

Контакты преподавателя: 
* Vk: https://vk.com/good_vladik — для организационных вопросов
* Tg: `@VVKarandashov` — для любых вопросов по java/backend/лабораторным/проектам
* Email: `vlad.karandashov.tech@gmail.com`
* Оставьте обратную связь по курсу: https://forms.gle/BA5hWZqoBWhUxBLv7
* * Любая обратная связь приветствуется и поощряется!
* * Вы можете рассчитывать на анонимность и непредвзятость!

Описание всех правил игры [читай здесь](assignments/README.md).

## Работа с репозиторием

- установите на компьютер ide для разработки (рекомендуются: intellij idea, OpenIDE)
- установите сами или через ide необходимые инструменты:
- - git
- - maven
- - любой JDK 25
- - желательно плагин lombok для ide
- установите docker (обязательно запускайте его daemon перед сборкой, некоторые тесты его используют)
- выполните сами или через ide клонирование репозитория
- соберите проект
- - `mvn clean install` в терминале
- - или через вкладку maven в ide -> lifecycle -> clean && install
- запускайте сервисы из класса Main или `java -jar example.jar`
- если для запуска требуется внешние БД или сервисы:
- - перейдите в подходящую папку внутри `infra` (`database`, `kafka`, `redis`, `s3`, `sso`, `temporal`)
- - выполните `docker compose up -d`
- - запускайте приложение, параметры подключения лежат в `application.properties` или `application.yml`в ресурсах соответствующего примера

Возможные ошибки [смотри здесь](tutorials/errors.md)

## Туториалы

* [Как добавить ssh ключ в git](tutorials/ssh.md)
* [Большой гайд как начать проект для лабораторных с помощью git](tutorials/startInGit.md)
* [Большой гайд как начать проект для лабораторных с помощью idea](tutorials/startInIde.md)
* [Как работать с git в idea + командная разработка](tutorials/workWithGit.md)

## Оглавление

### infra

- [database](infra/database): Пример запуска **актуальной версии** базы данных PostgreSQL с автоматическим созданием схем.
- [kafka](infra/kafka): Пример запуска локального кластера Apache Kafka с помощью docker compose.
- [debezium](infra/debezium): Пример реализации паттерна transactional outbox с помощью debezium. Пример запуска и конфигурации debezium.
- [redis](infra/redis): Пример запуска локального Redis (master + replica).
- [s3](infra/s3): Пример запуска локального S3-совместимого хранилища.
- [sso](infra/sso): Пример запуска отладочного локального SSO-сервера.
- [sso-oauth2-proxy](infra/sso-oauth2-proxy): Пример конфигурации OAuth2-прокси.
- [temporal](infra/temporal): Пример запуска и настройки Temporal.

### jbcd-1-core

Модуль для демонстрации базовых возможностей Java:

- **jbcd-1a-first-program**: Базовый пример Java-приложения с простой функцией сложения чисел. Демонстрирует основы синтаксиса Java и структуру проекта.
- **jbcd-1b-java-core**: Пример основных конструкций Java
- **jbcd-1b-oop**: Пример объектно-ориентированного программирования на сохранении документов в разные хранилища.
- **jbcd-1b-exceptions**: Пример checked/unchecked исключений, своих исключений и обработки ошибок через try/catch.
- **jbcd-1c-io**: Простой пример чтения CSV через BufferedReader и записи JSON через BufferedWriter.
- **jbcd-1c-collections**: Демонстрация массива и основных коллекций Java без Stream API.
- **jbcd-1d-reflection-annotation**: Демонстрация возможностей рефлексии и аннотаций
- **jbcd-1d-lombok**: Использование Lombok
- **jbcd-1d-mapstruct**: Использование MapStruct
- **jbcd-1d-mapstruct-lombok**: Использование MapStruct совместно с Lombok

### jbcd-2-client-server

Модуль для демонстрации работы с сетью:

- **jbcd-2-base-server-lib**: Общая библиотека с DTO, моделями и сервисами, используемая в примерах модуля.
- **jbcd-2a-netty-server**: Реализация HTTP-сервера на фреймворке Netty.
- **jbcd-2a-jooby-server**: Пример создания REST API с использованием фреймворка Jooby.
- **jbcd-2a-javalin-server**: Пример создания REST API с использованием фреймворка Javalin.
- **jbcd-2a-http-client**: Примеры использования стандартного Java HttpClient.
- **jbcd-2b-long-polling**: Реализация технологии Long Polling.
- **jbcd-2b-sse**: Пример использования Server-Sent Events (SSE).
- **jbcd-2b-websocket**: Двусторонний обмен данными через WebSockets.

### jbcd-3-database

Модуль для демонстрации работы с БД:

- **jbcd-3a-jdbc**: Пример работы с БД с помощью чистого JDBC.
- **jbcd-3b-pool-flyway**: Использование connection pool Hikari и инструмента миграции Flyway.
- **jbcd-3b-pool-liquibase**: Аналогичный пример с Liquibase, оставлен для сравнения подходов к миграциям.
- **jbcd-3c-jdbi**: Пример работы с простейшим ORM: Jdbi и миграциями Flyway.

### jbcd-4-spring

Модуль для демонстрации работы с фреймворком Spring:

- **jbcd-4a1-spring-core-configuration**: Конфигурация чистого Spring Core с использованием Java Config.
- **jbcd-4a2-spring-core-annotation**: Использование аннотаций (`@Component`, `@Service`, `@Repository` и др.) для управления бинами в Spring.
- **jbcd-4b-spring-boot-web**: Создание веб-приложений с использованием Spring Boot Starter Web + использование AOP.
- **jbcd-4c-spring-boot-data-jdbc**: Работа с базами данных через Spring Data JDBC и миграции Flyway.
- **jbcd-4c-spring-boot-jpa-hibernate**: Работа с базами данных через Spring Data JPA, Hibernate и миграции Flyway.

### jbcd-5-docs

Модуль для демонстрации способов документирования API:

- **jbcd-5a-code-first**: Генерация документации OpenAPI (Swagger) на основе существующего кода (подход Code First).
- **jbcd-5b-api-first**: Генерация кода на основе OpenAPI спецификации (подход API First).
- **open-api-service-spec.json**: Пример OpenAPI спецификации в формате JSON.
- **requests.puml**: Пример диаграммы взаимодействия в формате PlantUML.
- **requests.mermaid**: Пример диаграммы взаимодействия в формате Mermaid.

### jbcd-6-tools

Модуль для демонстрации backend инструментария:

- **jbcd-6a-kafka-simple**: Работа с kafka при помощи низкоуровневого официального apache-клиента.
- **jbcd-6a-kafka-spring**: Работа с kafka при помощи Spring Kafka.
- **jbcd-6b-kafka-transactional-outbox + test-debezium**: Реализация паттерна transactional outbox.
- **jbcd-6b-kafka-transactional-processor**: Пример работы с kafka-транзакциями.
- **jbcd-6c-redis-lettuce**: Демонстрация основных команд redis с помощью относительно-низкоуровнего клиента Lettuce.
- **jbcd-6c-redis-redisson**: Пример использования Redisson для нестандартных задач.
- **jbcd-6c-redis-spring-cache**: Кэш на основе Spring Cache + Redis и демонстрация pub/sub для SSE.
- **jbcd-6d-s3**: Пример работы с S3.
- **jbcd-6e-temporal**: Пример работы с Temporal.

### jbcd-7-advanced

Модуль для демонстрации продвинутой функциональности:

- **jbcd-7a-session-auth**: Полноценный демонстрационный пример приложения с сессионной безопасностью.
- **jbcd-7a-sso-jwt-auth + test-sso**: Пример аутентификации через JWT и выделенный sso.
- **jbcd-7a-sso-jwt-to-session-auth + test-sso**: Пример аутентификации через выделенный sso с обменом jwt на сессию.
- **jbcd-7a-user-data-from-headers + test-sso + test-sso-oauth2-proxy**: Пример получения данных пользователя из заголовков и аутентификации через oauth2-proxy.

### Демонстрационный проект

**jbcd-mini-project** - маленький демонстрационный проект на примере новостей.
Небольшое описание проекта [здесь](jbcd-mini-project/README.md).
