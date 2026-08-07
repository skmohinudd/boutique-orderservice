# boutique-orderservice

Creates orders, tracks order state and publishes order events through the outbox pattern.

## Overview

- **Type:** Spring Boot service
- **Stack:** Java 21, Spring Boot, Maven, JPA, PostgreSQL, Flyway, Actuator, Docker
- **Port:** `8084`

## Flow

```text
Client / service → Controller → Business logic → Database / events / downstream services
```

## Main APIs

```text
Get /orderId
Post /orderId/confirm
Post /orderId/payment-failed
```

## Database

```text
order_items
orders
outbox_events
```

## Configuration

```text
DB_CONNECTION_TIMEOUT_MS
DB_MAX_LIFETIME_MS
DB_PASSWORD
DB_POOL_MAX_SIZE
DB_POOL_MIN_IDLE
DB_URL
DB_USERNAME
DB_VALIDATION_TIMEOUT_MS
```

## Run

```bash
./mvnw spring-boot:run
./mvnw clean verify
```

## Docker

```bash
docker build -t boutique-orderservice:local .
```

## Health

```bash
curl http://localhost:8084/actuator/health
```

## CI/CD

This repository is built and deployed independently through its own GitHub Actions workflow.
