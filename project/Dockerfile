# base image
FROM openjdk:23-jdk-slim
# copy across build
COPY build/libs/project-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
