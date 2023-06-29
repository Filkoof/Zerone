FROM maven:3.8.4-jdk-11-slim as builder
WORKDIR /src
COPY . .

RUN mvn clean install -Dmaven.test.skip
FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=builder /src/target/a-tink-back-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "-Dserver.port=8081", "/app.jar"]
