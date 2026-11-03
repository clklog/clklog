FROM openjdk:8u342-jdk

VOLUME /tmp
ARG JAR_FILE
ENV JAVA_OPTS=
EXPOSE 8080
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
COPY ./iplib/* /iplib/
COPY ./project-setting.json /
COPY ${JAR_FILE} app.jar
