# Деплой на сервер

Здесь рассматривается план минимум!

## Сборка image

Используйте gitlab-ci или github-actions для сборки необходимых образов.
Можно сделать по аналогии с [.gitlab-ci.yml](../../.gitlab-ci.yml) если у вас gitlab.

## Создаете vps/vds

* Ресурсы сервера выбираете в зависимости от требований проекта и бюджета
* Обязательно нужен выделенный белый ip
* Возьмите бесплатный домен и привяжите его к вашему ip (A/AAAA-записи)
* Выберите ubuntu, если не уверены в своих силах

## Первоначальная настройка

Устанавливаем docker
```shell
curl -fsSL https://get.docker.com -o install-docker.sh
sudo sh install-docker.sh
sudo systemctl enable docker
sudo systemctl status docker
sudo usermod -aG docker $USER
sudo docker run hello-world
```

Пройдите аутентификацию, чтобы снять ограничения на скачивание image
```shell
docker login
```

Закрываем все порты, которые не используются:
```shell
sudo apt-get install ufw
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 80/udp
sudo ufw allow 443/tcp
sudo ufw allow 443/udp
sudo ufw enable
sudo ufw status
```
Должно получиться как-то так:
```
To                         Action      From
--                         ------      ----
22/tcp                     ALLOW       Anywhere                  
80/tcp                     ALLOW       Anywhere                  
80/udp                     ALLOW       Anywhere                  
443/tcp                    ALLOW       Anywhere                  
443/udp                    ALLOW       Anywhere 
```

## Запуск

Аутентифицируйтесь в github/gitlab если у вас приватный репозиторий.
[ssh.md](../../tutorials/ssh.md) - вот тут рассказывал как. 
Только теперь у вас сервер вместо личного ПК.

Клонируем свой репозиторий.

Запускаем `docker compose up -d --force-recreate`. 
Проверяем через `docker ps` и `docker logs <container_id>` работоспособность.

## Observability

В docker-compose включена связка Vector + VictoriaMetrics + VictoriaLogs + VictoriaTraces + Grafana.

Vector собирает только логи и метрики сервисов `api-news` и `api-captcha`:
* логи из Docker stdout отправляются в VictoriaLogs;
* метрики берутся с `/actuator/prometheus` и отправляются в VictoriaMetrics;
* трейсы сервисы отправляют напрямую в VictoriaTraces через OTLP HTTP.

Локально Grafana доступна на `http://localhost:3000`, логин/пароль: `admin`/`admin`.
На VPS Grafana проксируется через `https://grafana.news.karandashov.com`.

Datasource'ы и provisioned dashboards создаются автоматически при старте Grafana.
JSON-дашборды лежат в `server/grafana/dashboards`.
