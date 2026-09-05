# ScaleCart

A production-grade e-commerce microservices backend built with Java 17, Spring Boot 3.x, PostgreSQL, Redis, Kafka, RabbitMQ, and Docker.

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| api-gateway | 8080 | Single entry point, routes to downstream services |
| auth-service | 8081 | JWT + RSA authentication |
| product-service | 8082 | Product catalog with Redis caching |
| order-service | 8083 | Orders, cart, Kafka + RabbitMQ |
| payment-service | 8084 | Payments with idempotency keys |
| notification-service | — | Async email via Kafka consumer |
| report-service | 8086 | PDF invoice generation |

## Tech Stack

Java 17 | Spring Boot 3.x | PostgreSQL | Redis | Kafka | RabbitMQ | Docker | JMeter | Jenkins | AWS EC2

## Performance

Load-tested with Apache JMeter 5.6.3 (50–100 concurrent users).

- Redis cache: average latency **379 ms → 64 ms** (5.9x), throughput **2.0x**
- Product list API: **100** concurrent users, **500** requests, **0%** errors
- Payment idempotency: **10** concurrent requests, same key, **1** payment created
- APDEX: **0.944 (Excellent)**

Full numbers: [METRICS.md](METRICS.md)
