FROM adoptopenjdk/openjdk11:latest
EXPOSE 8000
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} enginedocker.jar
ENTRYPOINT ["java", "-jar", "/enginedocker.jar"]