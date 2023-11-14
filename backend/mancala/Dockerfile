FROM openjdk:21-jdk
WORKDIR /app
COPY target/*.jar /app/mancala-service.jar
EXPOSE 8081
CMD ["java", "-jar", "mancala-service.jar"]
