FROM flink:1.16-java8

ARG JAR_FILE

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

COPY iplib/*.txt /opt/flink/usrlib/iplib/
COPY project-setting.json /opt/flink/usrlib/project-setting.json
COPY ${JAR_FILE} /opt/flink/usrlib/clklog-processing-with-dependencies.jar
#RUN chmod +x /opt/flink/usrlib/clklog-processing-with-dependencies.jar
#add default iplib
#COPY iplib /opt/flink/usrlib/iplib