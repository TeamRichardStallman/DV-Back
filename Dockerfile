# 1. Use an official JDK image as the base image
FROM openjdk:17-jdk-slim AS build

# 2. Set environment variables
ENV APP_HOME=/app
WORKDIR $APP_HOME

# 3. Copy Gradle Wrapper and project files
COPY gradlew $APP_HOME/gradlew
COPY gradle $APP_HOME/gradle
COPY build.gradle $APP_HOME/
COPY src $APP_HOME/src

# 4. Give execution permission to Gradle Wrapper
RUN chmod +x ./gradlew

# 5. Build the application
RUN ./gradlew build --no-daemon

# 6. Use a smaller JRE image for the final container
FROM openjdk:17-jdk-slim

# 7. Set working directory
WORKDIR /app

# 8. Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# 9. Expose the port your application runs on (default: 8080)
EXPOSE 8080

#서버 환경 설정
ARG ENVIRONMENT=dev
ENV ENVIRONMENT=${ENVIRONMENT}

# 로그 디렉토리 생성
RUN mkdir /app/logs

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${ENVIRONMENT}"]