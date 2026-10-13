# Лабораторная работа №2: база данных

## Вариант 17

Сущность `Game`: `id (int)`, `title (String)`, `score (double)`.

## Реализация

Приложение работает с PostgreSQL через JDBC и предоставляет полный CRUD:

- `GameRepository` задаёт контракт из задания;
- `JdbcGameRepository` инкапсулирует JDBC и SQL-запросы;
- `HikariCP` управляет пулом соединений;
- `Flyway` создаёт схему, таблицу и хранит историю миграций;
- `Main` последовательно демонстрирует `save`, `findById`, `findByField`, `findAll`, `update` и идемпотентный `deleteById`;
- JDBC-ресурсы закрываются через `try-with-resources`, ошибки БД оборачиваются в `RepositoryException`.

## Запуск

Требуются JDK 21, Maven и Docker с плагином Compose.

```bash
docker compose up -d
mvn clean package
java -jar target/lab-2-1.0.0.jar
```

Остановить БД:

```bash
docker compose down
```

Удалить БД вместе с данными:

```bash
docker compose down -v
```

## Конфигурация

Значения по умолчанию находятся в `src/main/resources/application.properties`. Их можно переопределить переменными окружения, не меняя код:

| Переменная | Назначение |
|---|---|
| `DB_URL` | JDBC URL |
| `DB_USERNAME` | пользователь БД |
| `DB_PASSWORD` | пароль БД |
| `DB_SCHEMA` | схема БД |
| `DB_POOL_MAX_SIZE` | максимальный размер пула |
| `DB_POOL_MIN_IDLE` | минимальное число свободных соединений |
| `DB_CONNECTION_TIMEOUT_MS` | таймаут получения соединения |

В Docker Compose используются отдельные пользователь `game_app` и база `game_database`; стандартные пользователь `postgres` и схема `public` приложением не используются.

## Миграции

Миграции расположены в `src/main/resources/db/migration`. При запуске Flyway создаёт схему `game_catalog`, применяет ещё не выполненные миграции и проверяет их контрольные суммы. Таблица не пересоздаётся при повторном запуске.

## Тесты

Интеграционные тесты поднимают изолированный PostgreSQL через Testcontainers и проверяют все методы репозитория:

```bash
mvn test
```

Если Docker недоступен, Testcontainers корректно пропустит интеграционные тесты.
