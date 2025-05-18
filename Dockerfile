FROM openjdk:23-jdk-slim AS build
WORKDIR /app
VOLUME /tmp
ARG JAR_FILE=build/libs/CompanyReputationManagment-docker.jar
COPY ${JAR_FILE}  /app/company-reputation-management.jar
COPY src/main/resources/rsa/private.pem /app/resources/rsa/private.pem
COPY src/main/resources/rsa/public.pem /app/resources/rsa/public.pem

ENTRYPOINT ["java", "-jar", "/app/company-reputation-management.jar"]
