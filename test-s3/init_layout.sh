#!/bin/bash
# ================================================
#  Garage single-node — автоматическая инициализация
#  Запускать один раз после docker compose up -d
# ================================================

set -euo pipefail

# ──────────────── НАСТРОЙКИ (менять здесь) ────────────────
CONTAINER="garage-single"               # Имя контейнера из docker-compose.yml
CAPACITY="100GB"                        # Сколько места отдаём ноде (можно 1TB, 500GB, auto)
ZONE="dc1"                              # Зона размещения (для урока всегда dc1)
BUCKET_NAME="myfiles"                   # Имя первого бакета
KEY_NAME="student-key"                  # Имя первого API-ключа
MAKE_BUCKET_PUBLIC="yes"                # yes/no — делать ли бакет публичным (для веб-доступа)
# ──────────────────────────────────────────────────────────

echo "=== Определяем ID ноды ==="
NODE_ID=$(docker exec "$CONTAINER" /garage node id -q | cut -d' ' -f1)
SHORT_ID=${NODE_ID:0:8}
echo "Node ID: $NODE_ID"
echo "Short ID (для команд): $SHORT_ID"

echo "=== Назначаем layout (топологию) ==="
docker exec "$CONTAINER" /garage layout assign -z "$ZONE" -c "$CAPACITY" "$SHORT_ID"

echo "=== Проверяем новый layout ==="
docker exec "$CONTAINER" /garage layout show

echo "=== Применяем layout (ОБЯЗАТЕЛЬНО!) ==="
docker exec "$CONTAINER" /garage layout apply --version 1

echo "=== Проверяем статус кластера ==="
docker exec "$CONTAINER" /garage status

echo "=== Создаём API-ключ $KEY_NAME ==="
docker exec "$CONTAINER" /garage key create "$KEY_NAME" || true

echo "=== Создаём бакет $BUCKET_NAME ==="
docker exec "$CONTAINER" /garage bucket create "$BUCKET_NAME" || true

echo "=== Даём права ключу на бакет ==="
docker exec "$CONTAINER" /garage bucket allow "$BUCKET_NAME" --key "$KEY_NAME" --read --write

# Делаем бакет публичным, если нужно (для веб-доступа через :3902)
if [[ "$MAKE_BUCKET_PUBLIC" == "yes" ]]; then
  echo "=== Делаем бакет публичным (public read) ==="
  docker exec "$CONTAINER" /garage bucket allow "$BUCKET_NAME" --key "$KEY_NAME" --read
fi

echo "=== ГОТОВО! ==="
docker exec "$CONTAINER" /garage key info "$KEY_NAME" --show-secret
echo "Key ID и Secret — в выводе команды выше ↑"
echo "S3 endpoint:                  http://localhost:3900"
echo "Region:                       garage"