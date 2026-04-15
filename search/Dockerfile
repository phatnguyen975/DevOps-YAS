# Use the Eclipse Temurin JRE 21 base image
FROM eclipse-temurin:21-jre-alpine

# Install bash for the wait-for-it script
RUN apk add --no-cache bash

# Create a non-root user and group to run the application
RUN addgroup -S spring && adduser -S spring -G spring

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file and the wait-for-it script into the container, setting ownership to the non-root user
COPY --chown=spring:spring target/*.jar app.jar
COPY --chown=spring:spring wait-for-it.sh wait-for-it.sh

# Make the wait-for-it script executable
RUN chmod +x wait-for-it.sh

# Switch to the non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
