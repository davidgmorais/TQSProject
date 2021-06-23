FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} enginedocker.jar
ENTRYPOINT ["java", "-jar", "/enginedocker.jar"]