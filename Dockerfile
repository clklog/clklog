FROM openjdk:8

VOLUME /tmp
ARG JAR_FILE
ENV JAVA_OPTS=
ENTRYPOINT ["entrypoint.sh"]
EXPOSE 8002
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
COPY ./iplib/* /iplib/
COPY ./app-setting.json /
COPY ${JAR_FILE} app.jar
