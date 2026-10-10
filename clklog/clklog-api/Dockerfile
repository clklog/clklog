FROM openjdk:8u342-jdk

VOLUME /tmp
ARG JAR_FILE
ENV JAVA_OPTS=
ENTRYPOINT ["entrypoint.sh"]
EXPOSE 8080
COPY ./sql/* /sql/
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
COPY ${JAR_FILE} app.jar
