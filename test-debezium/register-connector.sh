#!/bin/sh
set -eu

echo "Waiting for Kafka Connect..."
until curl -fsS http://connect:8083/connectors > /dev/null; do
  sleep 2
done

echo "Registering Debezium PostgreSQL connector..."

curl -fsS -X PUT http://connect:8083/connectors/outbox-connector/config \
  -H 'Content-Type: application/json' \
  --data-binary @/producer.json

echo
echo "Connector registration finished."
