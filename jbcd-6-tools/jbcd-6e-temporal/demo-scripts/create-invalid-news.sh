#!/bin/sh
set -eu

NEWS_ID=${1:-$(uuidgen | tr '[:upper:]' '[:lower:]')}

curl -sS \
  -X POST http://localhost:8081/news \
  -H 'Content-Type: application/json' \
  -d '{
    "id": "'"$NEWS_ID"'",
    "title": "No",
    "message": "short"
  }' \
  -o /dev/null
echo
echo "Created invalid news with id: $NEWS_ID"
