# ── Stage 1: Build ────────────────────────────────────────────────────
# Full Maven + JDK image — used only for compiling
# This entire stage is DISCARDED after build — never in final image
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Set working directory inside the container
WORKDIR /build

# ── Dependency caching trick ──────────────────────────────────────────
# Copy parent pom first — it changes rarely
# Docker caches layers. If pom.xml hasn't changed, Maven dependencies
# are NOT re-downloaded on next build — saves 2-3 minutes per build
COPY pom.xml /build/pom.xml
COPY auth-service/pom.xml /build/auth-service/pom.xml

# Download dependencies (cached if pom.xml unchanged)
RUN mvn -f /build/auth-service/pom.xml dependency:go-offline -B \
    --no-transfer-progress

# Now copy source code (changes every build — so this layer isn't cached)
COPY auth-service/src /build/auth-service/src

# Build the JAR, skip tests (tests run in CI pipeline separately)
RUN mvn -f /build/auth-service/pom.xml package -DskipTests -B \
    --no-transfer-progress

# ── Stage 2: Runtime ──────────────────────────────────────────────────
# JRE only — no compiler, no Maven, no source code
# eclipse-temurin is the most commonly used JDK distribution in production
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Create non-root user — NEVER run production containers as root
# If an attacker breaks out of the app, they get a limited user not root
RUN addgroup -S scalecart && adduser -S scalecart -G scalecart

# Copy ONLY the JAR from the builder stage — nothing else
COPY --from=builder /build/auth-service/target/*.jar app.jar

# Copy RSA keys (needed at runtime for JWT signing)
COPY auth-service/src/main/resources/keys/ /app/keys/

# Switch to non-root user
USER scalecart

# Document which port this service uses (doesn't actually publish it)
EXPOSE 8081

# Health check — Docker monitors this, restarts container if unhealthy
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8081/actuator/health || exit 1

# Run the JAR
# -Xmx256m: max heap 256MB — prevents one container eating all memory
# -Djava.security.egd: faster random number generation (common JVM optimization)
ENTRYPOINT ["java", \
    "-Xmx256m", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]