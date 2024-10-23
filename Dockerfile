FROM amazoncorretto:23-jdk as builder
ARG JAR_FILE=build/libs/\*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]