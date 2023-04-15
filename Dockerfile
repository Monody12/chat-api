FROM openjdk:17
VOLUME /tmp
COPY target/chat-api.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
