#!/bin/bash

# Не забудь запустить один из примеров сервера (netty/javalin)

HOST="localhost"
PORT="8080"
URL="http://$HOST:$PORT/news"

echo "Testing server at $URL"

echo "1. Creating news..."
RESPONSE=$(curl -s -X POST "$URL" \
     -H "Content-Type: application/json" \
     -d '{"title": "Test Title", "message": "Test Message Body"}')
echo "Response: $RESPONSE"

# вытаскиваем id созданной новости
ID=$(echo "$RESPONSE" | grep -oE '[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}' | head -n 1)
if [ -z "$ID" ]; then
    echo "Error: Failed to extract ID from response"
    exit 1
fi
echo "Extracted ID: $ID"
echo "-----------------------------------"

echo "2. Getting news by ID ($ID)..."
curl -s -X GET "$URL/$ID"
echo -e "\n-----------------------------------"

echo "3. Getting all news..."
curl -s -X GET "$URL"
echo -e "\n-----------------------------------"