# --- Stage 1: Build Stage using Maven with OpenJDK 17 ---
# This image contains Maven 3.8.4 and OpenJDK 17 pre-installed.
# It is used to compile and package the Spring Boot application.
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory inside the container to /app
WORKDIR /app

# Copy only the pom.xml first so Docker can cache the dependencies
COPY pom.xml .

# Download all project dependencies (goes offline later for faster rebuilds)
# This helps to cache dependencies in Docker layer for better rebuild performance
RUN mvn dependency:go-offline

# Now copy the actual source code into the container
COPY src ./src

# Build the Spring Boot application and skip tests for faster build
RUN mvn clean package -DskipTests


# --- Stage 2: Runtime Stage using slim OpenJDK 17 ---
# This is a lightweight image just to run the packaged JAR. No Maven here.
FROM openjdk:17-jdk-slim

# Set working directory again
WORKDIR /app

# Copy the built JAR file from the build stage into the runtime image
COPY --from=build /app/target/authify-0.0.1-SNAPSHOT.jar .

# Expose port 8888 to allow access from outside the container
EXPOSE 8888

# Define the command to run the Spring Boot JAR when the container starts
ENTRYPOINT ["java", "-jar", "/app/authify-0.0.1-SNAPSHOT.jar"]
