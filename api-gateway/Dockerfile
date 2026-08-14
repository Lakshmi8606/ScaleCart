FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml /build/pom.xml
COPY api-gateway/pom.xml /build/api-gateway/pom.xml
RUN mvn -f /build/api-gateway/pom.xml dependency:go-offline -B \
    --no-transfer-progress
COPY api-gateway/src /build/api-gateway/src
RUN mvn -f /build/api-gateway/pom.xml package -DskipTests -B \
    --no-transfer-progress

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S scalecart && adduser -S scalecart -G scalecart
COPY --from=builder /build/api-gateway/target/*.jar app.jar
COPY api-gateway/src/main/resources/keys/ /app/keys/
USER scalecart
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx256m", \
    "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]