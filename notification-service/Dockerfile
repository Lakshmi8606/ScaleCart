FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml /build/pom.xml
COPY notification-service/pom.xml /build/notification-service/pom.xml
RUN mvn -f /build/notification-service/pom.xml dependency:go-offline -B \
    --no-transfer-progress
COPY notification-service/src /build/notification-service/src
RUN mvn -f /build/notification-service/pom.xml package -DskipTests -B \
    --no-transfer-progress

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S scalecart && adduser -S scalecart -G scalecart
COPY --from=builder /build/notification-service/target/*.jar app.jar
USER scalecart
# No EXPOSE — notification service has no HTTP endpoints
ENTRYPOINT ["java", "-Xmx128m", \
    "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]