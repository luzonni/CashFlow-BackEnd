FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /build/target/quarkus-app/ ./quarkus-app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quarkus-app/quarkus-run.jar"]