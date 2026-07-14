FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . /app

RUN chmod +x gradlew && ./gradlew :applications:app-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S walletgroup && adduser -S walletuser -G walletgroup
USER walletuser

COPY --from=builder /app/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]