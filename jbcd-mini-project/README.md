# jbcd-mini-project

### Архитектура проекта

```mermaid
graph TD
    User((Пользователь))
    Browser[Frontend: WEB SPA]

    subgraph "Backend"
        NewsAPI[api-news]
        CaptchaAPI[api-captcha]
    end

    subgraph "Data Tier"
        PostgreSQL[(PostgreSQL: News storage)]
        Redis[(Redis: Captcha & Cache)]
    end

    User --> Browser
    Browser -- " HTTP REST API " --> NewsAPI
    NewsAPI -- " Internal call " --> CaptchaAPI
    Browser -- " HTTP REST API " --> CaptchaAPI
    NewsAPI -- " Spring Data JDBC " --> PostgreSQL
    NewsAPI -- " Spring Cache " --> Redis
    CaptchaAPI -- " Spring Data Redis " --> Redis
```

### API

Проект состоит из нескольких микросервисов, каждый из которых предоставляет свой REST API.

| Сервис                                               | Описание                                         |
|------------------------------------------------------|--------------------------------------------------|
| [api-news](./api-news/README.md)                     | Управление новостями                             |
| [api-captcha](./api-captcha/README.md)               | Генерация и проверка капчи                       |
| [lib-client-captcha](./lib-client-captcha/README.md) | Клиент api-captcha для вызова из других сервисов |
| [lib-utils-web](./lib-utils-web/README.md)           | Общие утилиты/стандарты обработки ошибок         |
| [view-news](./view-news/README.md)                   | Frontend приложение                              |

### Процесс создания новости

```mermaid
sequenceDiagram
    autonumber
    participant U as Пользователь
    participant F as Browser-frontend (view-news SPA)
    participant AC as API Captcha
    participant AN as API News
    U ->> F: Переходит на вкладку создания новости
    F ->> U: Отображает форму создания (без капчи)
    U ->> F: Пишет новость, нажимает "Хочу отправить"
    F ->> AC: POST /captcha/generate
    AC -->> F: {id, imageBase64} (В Redis: CREATED, TTL 5 мин)
    F ->> U: Отображает капчу пользователю
    U ->> F: Вводит решение, нажимает "Отправить"
    F ->> AC: POST /captcha/{id}/solve {answer}

    alt Решение верное
        AC -->> F: {solved: true} (В Redis: SOLVED, TTL 10 сек)
        F ->> AN: POST /news {title, message, solvedCaptchaId}
        AN ->> AC: POST /internal/captcha/{id}/checkAndExpire
        AC -->> AN: {isValid: true} (Запись удалена из Redis)
        AN -->> F: 200 OK (Новость сохранена в БД)
        F ->> U: Успех, переход к списку новостей
    else Решение неверное или ошибка
        AC -->> F: {solved: false} (Запись удалена из Redis)
        F ->> AC: POST /captcha/generate (Авто-регенерация)
        AC -->> F: {id, imageBase64}
        F ->> U: Показывает ошибку и новую капчу
    end
```

### Деплой

Для локальной разработки необходимо пользоваться [local-dev](server/local-dev/docker-compose.yml).
Роль gateway выполняет простой nginx-reverse-proxy. Openobserve торчит портом 5080 наружу.

Для развертывания на сервере необходимо пользоваться [vps](server/vps) конфигурацией
и [вот этим туториалом](server/README.md).