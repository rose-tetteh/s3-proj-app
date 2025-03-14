#Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY .env .
RUN mvn clean package -DskipTests

#Run stage
FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY --from=build /app/target/s3-proj-app-*.jar /app/s3-proj-app.jar
COPY .env .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "s3-proj-app.jar"]