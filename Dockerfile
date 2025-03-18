# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/s3-proj-app-*.jar /app/s3-proj-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "s3-proj-app.jar"]