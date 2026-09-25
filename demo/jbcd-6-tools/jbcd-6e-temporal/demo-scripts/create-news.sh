#!/bin/sh
set -eu

NEWS_ID=${1:-$(uuidgen | tr '[:upper:]' '[:lower:]')}

curl -sS \
  -X POST http://localhost:8081/news \
  -H 'Content-Type: application/json' \
  -d '{
    "id": "'"$NEWS_ID"'",
    "title": "Temporal demo",
    "message": "This news is waiting for approval in Temporal."
  }' \
  -o /dev/null
echo
echo "Created news with id: $NEWS_ID"
