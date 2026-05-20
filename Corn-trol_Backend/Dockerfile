FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache ffmpeg

WORKDIR /app

COPY build/libs/corntrol-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]