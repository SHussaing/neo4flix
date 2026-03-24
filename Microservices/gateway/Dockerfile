# Build stage
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY Microservices/gateway/pom.xml .
COPY Microservices/gateway/.mvn .mvn
COPY Microservices/gateway/mvnw mvnw
COPY Microservices/gateway/mvnw.cmd mvnw.cmd
COPY Microservices/gateway/src src
RUN ./mvnw -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
# Copy certs into image (no volume mount needed)
COPY certs /certs
EXPOSE 8443
ENTRYPOINT ["java","-jar","/app/app.jar"]
