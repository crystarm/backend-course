#!/bin/sh
set -eu

if [ $# -ne 1 ]; then
  echo "Usage: $0 <news-id>"
  exit 1
fi

curl -sS "http://localhost:8081/news/$1"
echo
