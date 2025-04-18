FROM openjdk:17-alpine
WORKDIR /app
COPY ./target/*.jar /app/app.jar
EXPOSE 8080 5005
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n", "app.jar"]