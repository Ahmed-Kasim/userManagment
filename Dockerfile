# Use an official Maven image to build the application
FROM maven:3.8.6-jdk-17-slim AS build

# Set the working directory
WORKDIR /app

# Copy your project files into the container
COPY . .

# Build your Java application
RUN mvn clean install

# Use an official openjdk image to run the application
FROM openjdk:17-jdk-slim

# Copy the JAR file from the build image
COPY --from=build target/userManagment-0.0.1-SNAPSHOT.jar.original target/userManagment-0.0.1-SNAPSHOT.jar.original

# Expose port (use the port your Spring Boot app is running on)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "target/userManagment-0.0.1-SNAPSHOT.jar.original"]
