# Build Time
FROM maven:3.8-adoptopenjdk-11 as build
WORKDIR /app
COPY .. .
RUN mvn clean install -P integration-tests

# Run Time
FROM adoptopenjdk/openjdk11:ubi
WORKDIR /app
COPY --from=build /app/target/*.jar service.jar
CMD ["java", "-jar", "/app/service.jar"]
