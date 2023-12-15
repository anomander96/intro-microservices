# Use a base image with Java 8
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the necessary files into the container
COPY target/resource-service.jar /app/resource-service.jar
# Adjust the JAR name and path as per your project setup

# Expose the port the service runs on
EXPOSE 8081
# Change the port if your service runs on a different port

# Command to run the service when the container starts
CMD ["java", "-jar", "resource-service.jar"]
# Adjust the command as required to start your service
