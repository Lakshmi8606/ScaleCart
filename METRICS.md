# ScaleCart Performance Metrics

Generated: 5 September 2026
Environment: Local (Windows, Docker Compose)
Tool: Apache JMeter 5.6.3

---

## 1. Redis Cache Performance — GET /api/products/{id}

| Metric | Without Cache (Cold) | With Cache (Warm) | Improvement |
|--------|---------------------|-------------------|-------------|
| Average Latency | 379 ms | 64 ms | 5.9x faster |
| p90 Latency | 358 ms | 129 ms | 2.8x faster |
| p99 Latency | 5889 ms | 325 ms | 18.1x faster |
| Throughput | 99.0 req/sec | 197.8 req/sec | 2.0x higher |

Test config: 50 concurrent users, 5s ramp-up, 20 loops (1000 total requests)

---

## 2. Product API Throughput — GET /api/products

| Metric | Value |
|--------|-------|
| Concurrent Users | 100 |
| Total Requests | 500 |
| Throughput | 50.2 req/sec |
| p99 Latency | 3331 ms |
| Error Rate | 0% |

Test config: 100 users, 10s ramp-up, 5 loops, `page=0&size=10`

---

## 3. Payment Idempotency Under Concurrent Load

| Metric | Value |
|--------|-------|
| Concurrent requests with same key | 10 |
| Payments created in DB | 1 |
| Duplicate payments | 0 |
| Data integrity maintained | Yes |

Note: Zero duplicate payments under 10 concurrent requests with identical idempotency key (`jmeter-concurrent-test-fixed-key`). The database unique constraint prevented a double charge.

---

## 4. APDEX Score: 0.944 (Excellent)

From the headless JMeter HTML dashboard (`cache-cli-results.jtl`). Toleration threshold 500 ms, frustration threshold 1.5 s. Requests summary: 100% pass.

---

## 5. Test Coverage

| Service | Service Layer | Overall |
|---------|--------------|---------|
| Auth Service | 75% branch | ~60% |
| Product Service | 67% instruction | ~60% |
| Payment Service | 89% instruction | ~50% |
| Order Service | 70%+ | ~50% |

Total unit + integration + controller tests: 50+

---

## 6. Build & Deploy

| Metric | Value |
|--------|-------|
| Docker image size (per service) | ~180MB |
| Maven build time (with cache) | ~30 sec |
| Services | 7 (6 microservices + API gateway) |

Headless JMeter (CI-style) HTML report: `scalecart-results/report-output/index.html`
