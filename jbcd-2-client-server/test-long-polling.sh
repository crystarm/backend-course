#!/bin/bash

# Не забудь запустить пример jbcd-2b-long-polling

HOST="localhost"
PORT="8080"
URL="http://$HOST:$PORT"

# while true делаем get /listen запросы и фиксируем результат

echo "Listening for new news events at $URL/listen..."
echo "Press [CTRL+C] to stop."

while true; do
    echo "[$(date +%H:%M:%S)] Waiting for message..."
    RESPONSE=$(curl -s -i -X GET "$URL/listen")
    
    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP/" | awk '{print $2}')
    BODY=$(echo "$RESPONSE" | sed -e '1,/^\r$/d')

    if [ "$HTTP_STATUS" == "200" ]; then
        echo "[$(date +%H:%M:%S)] Got message: $BODY"
    elif [ "$HTTP_STATUS" == "404" ]; then
        echo "[$(date +%H:%M:%S)] Timeout (no messages)"
    else
        echo "[$(date +%H:%M:%S)] Error: $HTTP_STATUS"
        echo "$BODY"
        sleep 1
    fi
    echo "-----------------------------------"
done

