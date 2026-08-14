FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml /build/pom.xml
COPY product-service/pom.xml /build/product-service/pom.xml
RUN mvn -f /build/product-service/pom.xml dependency:go-offline -B \
    --no-transfer-progress
COPY product-service/src /build/product-service/src
RUN mvn -f /build/product-service/pom.xml package -DskipTests -B \
    --no-transfer-progress

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S scalecart && adduser -S scalecart -G scalecart
COPY --from=builder /build/product-service/target/*.jar app.jar
COPY product-service/src/main/resources/keys/ /app/keys/
USER scalecart
EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8082/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx256m", \
    "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]