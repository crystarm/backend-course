#!/bin/bash

# Не забудь запустить пример jbcd-2b-sse

HOST="localhost"
PORT="8080"
URL="http://$HOST:$PORT"

# делаем sse /listen запросы и фиксируем результат (перезапускаем если упало)

echo "Listening for SSE events at $URL/listen..."
echo "Press [CTRL+C] to stop."

while true; do
    echo "[$(date +%H:%M:%S)] Connecting to SSE stream..."
    # -N / --no-buffer: отключает буферизацию вывода, чтобы видеть события сразу
    # -s: бесшумный режим
    # --http1.1: форсируем HTTP/1.1 (иногда полезно для SSE, хотя Jooby разберется)
    curl -N -s "$URL/listen"
    
    EXIT_CODE=$?
    if [ $EXIT_CODE -ne 0 ]; then
        echo "[$(date +%H:%M:%S)] Connection lost or failed (exit code $EXIT_CODE). Reconnecting in 2 seconds..."
        sleep 2
    else
        echo "[$(date +%H:%M:%S)] Connection closed by server. Reconnecting..."
    fi
    echo "-----------------------------------"
done

