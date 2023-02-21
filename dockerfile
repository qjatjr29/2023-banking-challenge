FROM adoptopenjdk/openjdk11:alpine

EXPOSE 8080

ARG JAR_FILE=build/libs/banking-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar","/app.jar"]

# java -jar -Dspring.profiles.active==prod app.jar