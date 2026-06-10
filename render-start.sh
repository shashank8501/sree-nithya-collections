#!/usr/bin/env sh
set -eu

export INTERNAL_API_TOKEN="${INTERNAL_API_TOKEN:-demo-internal-token}"
export ADMIN_USERNAME="${ADMIN_USERNAME:-admin}"
export ADMIN_PASSWORD="${ADMIN_PASSWORD:-Admin@12345}"

java -jar /app/product-service.jar \
  --spring.profiles.active=render \
  --server.port=8081 \
  --internal.api.token="$INTERNAL_API_TOKEN" &

java -jar /app/cart-service.jar \
  --spring.profiles.active=render \
  --server.port=8082 \
  --product.service.url=http://localhost:8081 &

java -jar /app/order-service.jar \
  --spring.profiles.active=render \
  --server.port=8083 \
  --internal.api.token="$INTERNAL_API_TOKEN" &

java -jar /app/storefront-service.jar \
  --spring.profiles.active=render \
  --server.port="${PORT:-8080}" \
  --product.service.url=http://localhost:8081 \
  --cart.service.url=http://localhost:8082 \
  --order.service.url=http://localhost:8083 \
  --internal.api.token="$INTERNAL_API_TOKEN" \
  --admin.username="$ADMIN_USERNAME" \
  --admin.password="$ADMIN_PASSWORD"
