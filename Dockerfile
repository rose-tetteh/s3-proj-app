#Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#Run stage
FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY --from=build /app/target/s3-proj-app-*.jar /app/s3-proj-app.jar
EXPOSE 8080

# Accept build arguments (names coming from GitHub secrets)
ARG G_AWS_ACCESS_KEY
ARG G_AWS_SECRET_KEY
ARG G_AWS_REGION
ARG G_AWS_BUCKET_NAME

# Map the build arguments to the environment variables expected by your application
ENV AWS_ACCESS_KEY=${G_AWS_ACCESS_KEY}
ENV AWS_SECRET_KEY=${G_AWS_SECRET_KEY}
ENV AWS_REGION=${G_AWS_REGION}
ENV AWS_BUCKET_NAME=${G_AWS_BUCKET_NAME}

ENTRYPOINT ["java", "-jar", "s3-proj-app.jar"]