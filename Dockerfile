FROM openjdk:8

VOLUME /tmp
ARG JAR_FILE
ENV JAVA_OPTS=
ENTRYPOINT ["entrypoint.sh"]
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
COPY ${JAR_FILE} app.jar
