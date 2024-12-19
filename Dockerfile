# 1. Use an official JDK image as the base image
FROM openjdk:17-jdk-slim AS build

# 2. Set environment variables
ENV APP_HOME=/app
WORKDIR $APP_HOME

# 3. Copy build script and Gradle Wrapper (rarely changes)
COPY gradlew $APP_HOME/gradlew
COPY gradle $APP_HOME/gradle
COPY build.gradle $APP_HOME/

# 4. Give execution permission to Gradle Wrapper
RUN chmod +x ./gradlew

# 5. Download dependencies (uses cache unless build.gradle changes)
RUN ./gradlew dependencies --no-daemon

# 6. Copy application source code (changes frequently)
COPY src $APP_HOME/src
COPY .env $APP_HOME/.env

# 7. Build the application
RUN ./gradlew build --no-daemon

# 8. Use a smaller JRE image for the final container
FROM openjdk:17-jdk-slim

# 9. Set working directory
WORKDIR /app

# 10. Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# 11. Expose the port your application runs on (default: 8080)
EXPOSE 8080

# 12. Server environment setup
ARG ENVIRONMENT=dev
ENV ENVIRONMENT=${ENVIRONMENT}

# 13. Create logs directory
RUN mkdir /app/logs

# 14. Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${ENVIRONMENT}"]