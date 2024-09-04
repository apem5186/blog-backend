# backend/Dockerfile

# Use the official Gradle image to build the application
FROM gradle:8.8-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy all project files to the working directory
COPY . .

# Ensure gradlew has execute permissions
RUN chmod +x ./gradlew

# Clean the build to remove old/generated files
RUN ./gradlew clean --no-daemon

# Build the Spring Boot application
RUN ./gradlew bootJar --no-daemon

# Use the official OpenJDK image for runtime
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the backend port
EXPOSE 8081

# Run the Spring Boot application
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]