#!/usr/bin/env sh
set -eu

export ADMIN_USERNAME="${ADMIN_USERNAME:-admin}"
export ADMIN_PASSWORD="${ADMIN_PASSWORD:-Admin@12345}"

java -jar /app/storefront-service.jar \
  --spring.profiles.active=render \
  --server.port="${PORT:-8080}" \
  --render.single-service=true \
  --admin.username="$ADMIN_USERNAME" \
  --admin.password="$ADMIN_PASSWORD"
