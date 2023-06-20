# Build stage
FROM maven:3.8.4-openjdk-17-slim AS builder

# Install Git
RUN apt-get update && \
    apt-get install -y git
# Set the working directory in the container
WORKDIR /app

# Clone the repository
RUN git clone https://github.com/hey-world-team/currencyConverter.git

# Set the working directory to the cloned repository
WORKDIR /app/currencyConverter

# Build the application with Maven
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/currencyConverter/target/currency_converter-0.0.1.jar ./app.jar

# Expose the port on which the Spring Boot application listens
EXPOSE 8081

# Define the command to run the application
CMD ["java", "-jar", "app.jar"]
