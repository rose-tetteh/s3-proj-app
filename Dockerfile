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

# Create AWS credentials directory
RUN mkdir -p /root/.aws

# Create AWS credentials file
RUN echo "[default]" > /root/.aws/credentials \
    && echo "aws_access_key_id=${G_AWS_ACCESS_KEY}" >> /root/.aws/credentials \
    && echo "aws_secret_access_key=${G_AWS_SECRET_KEY}" >> /root/.aws/credentials

# Create AWS config file (optional, but prevents the previous error)
RUN echo "[default]" > /root/.aws/config \
    && echo "region=eu-west-1" >> /root/.aws/config

# Ensure correct permissions
RUN chmod 600 /root/.aws/credentials
RUN chmod 600 /root/.aws/config

ENTRYPOINT ["java", "-jar", "s3-proj-app.jar"]