#!/bin/sh
set -eu

if [ $# -ne 1 ]; then
  echo "Usage: $0 <news-id>"
  exit 1
fi

curl -sS \
  -X POST http://localhost:8081/news/status \
  -H 'Content-Type: application/json' \
  -d "{
    \"newsId\": \"$1\",
    \"status\": \"REJECTED\"
  }"
echo
