# Build stage
FROM maven:3.8.4-openjdk-17-slim AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the local source code to the container
COPY . /app

# Build the application with Maven
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/currency_converter-0.0.1.jar ./app.jar

# Expose the port on which the Spring Boot application listens
EXPOSE 8081

# Define the command to run the application
CMD ["java", "-jar", "app.jar"]
