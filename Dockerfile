FROM openjdk:23-jdk-slim AS build
WORKDIR /app
VOLUME /tmp
ARG JAR_FILE=build/libs/CompanyReputationManagment-docker.jar
COPY ${JAR_FILE}  /app/company-reputation-management.jar
COPY src/main/resources/rsa /app/resources/rsa

ENV PRIVATE_KEY_PATH=/app/resources/rsa/private.pem

ENTRYPOINT ["java", "-jar", "/app/company-reputation-management.jar"]
