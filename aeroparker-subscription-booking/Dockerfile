FROM adoptopenjdk/openjdk8:alpine-slim
VOLUME /tmp
ARG JAR_FILE
ARG SPRING_PROFILE
ENV PROFILE ${SPRING_PROFILE}
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=${PROFILE}","-Djava.security.egd=file:/dev/./urandom","-Xms32m","-Xmx300m","-XX:+UseG1GC","-jar","/app.jar"]