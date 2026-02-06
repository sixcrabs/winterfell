FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD app/{project.name}-{project.version}.jar app.jar
EXPOSE 8800
ENV JAVA_OPTS="" ACTIVE_PROFILE="prod"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$ACTIVE_PROFILE -jar /app.jar" ]