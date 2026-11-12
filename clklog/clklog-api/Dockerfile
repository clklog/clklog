FROM eclipse-temurin:17-jre

VOLUME /tmp
ARG JAR_FILE
ENV JAVA_OPTS=
EXPOSE 8080
COPY ./sql/* /sql/
COPY docker-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
COPY ${JAR_FILE} app.jar
