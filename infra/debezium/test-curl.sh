#!/bin/bash

set -euo pipefail

# Не забудь запустить docker compose up -d

HOST="localhost"
PORT="8080"
URL="http://$HOST:$PORT/news"

echo "Testing server at $URL"

echo "1. Creating news..."
RESPONSE=$(curl --fail-with-body --silent --show-error -X POST "$URL" \
    -H "Content-Type: application/json" \
    -d '{"title":"Test Title","message":"Test Message Body"}')
echo "Response: $RESPONSE"
