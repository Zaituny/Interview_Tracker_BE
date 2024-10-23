FROM gradle:jdk23 as build
COPY src /home/app/src
COPY build.gradle /home/app
RUN gradle -p /home/app build

FROM amazoncorretto:23-jdk
ARG JAR_FILE=build/libs/\*.jar
COPY --from=build /home/app/${JAR_FILE} /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]