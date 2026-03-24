# Build stage
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Copy Maven wrapper + project files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY src src

# Maven wrapper will download Maven distribution on first run
RUN ./mvnw -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
