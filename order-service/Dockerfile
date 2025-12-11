# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy all module poms (Maven needs them to validate parent POM)
COPY common-events/pom.xml ./common-events/
COPY gateway/pom.xml ./gateway/
COPY user-service/pom.xml ./user-service/
COPY order-service/pom.xml ./order-service/

# Copy source code for common-events and order-service (the module we're building)
COPY common-events/src ./common-events/src
COPY order-service/src ./order-service/src

# Create empty src directories for other modules (to satisfy Maven module validation)
RUN mkdir -p gateway/src/main/java gateway/src/main/resources && \
    mkdir -p user-service/src/main/java user-service/src/main/resources

# Build the project
RUN mvn clean package -DskipTests -pl order-service -am

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar
COPY --from=build /app/order-service/target/order-service-*.jar app.jar

# Expose port
EXPOSE 8091

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

