FROM openjdk:17-alpine
ARG JAR_FILE=target/zero-0.0.1-SNAPSHOT.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","app.jar"]