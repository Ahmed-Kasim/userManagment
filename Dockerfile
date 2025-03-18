# Use an official Maven image to build the application
FROM maven:3.8.6-openjdk-17-slim AS build

# Set the working directory
WORKDIR /app

# Copy your project files into the container
COPY . .

# Build your Java application
RUN mvn clean install

# Use an official openjdk image to run the application
FROM openjdk:17-jdk-slim

# Copy the JAR file from the build image
COPY --from=build /app/target/your-application-name.jar /app/your-application-name.jar

# Expose port (use the port your Spring Boot app is running on)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/your-application-name.jar"]
