FROM eclipse-temurin:11-jre
ARG JAR_FILE
WORKDIR /app
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
