FROM flink:1.19.3-java17

ARG JAR_FILE

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

RUN mkdir -p /opt/flink/usrlib/iplib
COPY ${JAR_FILE} /opt/flink/usrlib/clklog-processing-with-dependencies.jar
# COPY 会保留源文件权限位：CI 展平脚本/工件下载可能产生 600 权限的 jar，
# 而 Flink 以 flink(9999) 用户运行，必须保证全局可读
RUN chmod 644 /opt/flink/usrlib/clklog-processing-with-dependencies.jar
#add default iplib
#COPY iplib /opt/flink/usrlib/iplib
