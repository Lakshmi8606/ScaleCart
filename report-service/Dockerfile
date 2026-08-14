FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml /build/pom.xml
COPY report-service/pom.xml /build/report-service/pom.xml
RUN mvn -f /build/report-service/pom.xml dependency:go-offline -B \
    --no-transfer-progress
COPY report-service/src /build/report-service/src
RUN mvn -f /build/report-service/pom.xml package -DskipTests -B \
    --no-transfer-progress

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S scalecart && adduser -S scalecart -G scalecart
COPY --from=builder /build/report-service/target/*.jar app.jar
USER scalecart
EXPOSE 8086
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8086/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx256m", \
    "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]