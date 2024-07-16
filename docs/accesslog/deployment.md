
# 源码地址
  
## 环境准备

- Linux服务器（本文档以ubuntu为例）
- JDK 1.8
- Nginx 1.18
- Redis 3.2.4
- Zookeeper 3.7.2
- Kafka 2.12-3.3.1
- Clickhouse 23.2.1
- Flink 1.16.3 （可选）
  
[环境初始化步骤参考](../quickstart/preparation.md#初始化步骤参考)

## 部署步骤

### 1.创建数据库

 在Clickhouse中创建数据库，执行脚本如下：

 ```
 --数据库
CREATE DATABASE IF NOT EXISTS accesslogdb ENGINE = Atomic;

--表
CREATE TABLE accesslogdb.gp_nginx_access
(
    `country` String,
    `upstream_uri` String,
    `upstream_addr` String,
    `uri` String,
    `request_method` String,
    `http_host` String,
    `http_user_agent` String,
    `stat_hour` String,
    `manufacturer` String,
    `remote_user` String,
    `upstream_status` String,
    `request_time` Float32,
    `province` String,
    `browser` String,
    `model` String,
    `browser_version` String,
    `brand` String,
    `remote_addr` String,
    `stat_date` Date,
    `stat_min` String,
    `time_local` DateTime64(3),
    `http_version` String,
    `city` String,
    `body_sent_bytes` String,
    `http_referrer` String,
    `server_name` String,
    `upstream_response_time` String,
    `status` String,
    `application_code` String,
    `create_time` DateTime64(3) DEFAULT now()
)
ENGINE = MergeTree
PARTITION BY stat_date
ORDER BY (application_code, http_host, status, request_method, remote_addr, country, province, city, uri, browser, time_local)
SETTINGS index_granularity = 8192

 ```

### 2.采集nginx的access日志

使用vector或filebeat或其它采集工具采集nginx的access日志，写入kafka,topic为accesslog。

### 3.部署accesslog-processing 服务

    accesslog-processing 服务可选择是否使用flink。

    不使用flink，则部署accesslog-processing-simple服务。

    使用flink，则部署accesslog-processing服务。

#### accesslog-processing-sample 服务部署

1. 编译应用程序
2. 上传程序文件

    在目录`/usr/local/services/`中创建`accesslog-processing-simple`目录并将文件包`accesslog-processing-simple.jar`、配置文件`application.yml`以及源码中的`iplib`文件夹拷贝进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir accesslog-processing-simple

    ```

   - 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码

    ```
    chmod 500 accesslog-processing-simple.jar
    ```

3. 修改配置文件
    根据前面部署的组件配置，修改`application.yml`中`redis`、`kafka`、`clickhouse`相关配置，代码如下：

    ```
    server:
    port: 8004
    logging:
    file:
        path: log
    spring:
    redis:
        # 单机配置
        host: localhost
        port: 6379
        # password
        # 哨兵配置
        # sentinel
        # master: gct
        # nodes: 10.100.2.1:26379,10.100.2.2:26379,10.100.2.3:26379
    application.name: accesslog-processing-simple
    datasource:
        clickhouse:
        jdbc-url: jdbc:clickhouse://localhost:8123/accesslogdb
        username: default
        password:

    accesslog:
    # 处理线程数
    thread-count: 2

    # kafka配置
    kafka-bootstrap-servers: localhost:9092
    kafka-consumer-group-id: accesslog-group
    topic-name: accesslog
    nginx_access_table: gp_nginx_access

    # 匹配accesslog的正则表达式
    accesslog-grok: '%{IPORHOST:remote_addr} - \[%{DATA:x_forward_for}\] - %{DATA:remote_user} \[%{HTTPDATE:time}\] \"%{WORD:request_method} %{DATA:uri} HTTP/%{NUMBER:http_version}\" %{NUMBER:status} %{NUMBER:body_sent_bytes} \"%{DATA:http_referrer}\" \"%{DATA:http_user_agent}\" %{NUMBER:request_length} %{NUMBER:request_time} \[%{DATA:proxy_upstream_name}\] %{DATA:upstream_addr} %{DATA:upstream_response_length} %{DATA:upstream_response_time} %{DATA:upstream_status} %{DATA:req_id} %{DATA:host} \[\]'

    # 服务器名
    server-name: accesslog

    # 应用名
    application-code: accesslog

    ```

4. 创建服务

   ```
    vim /etc/systemd/system/accesslog-processing-simple.service

    [Unit]
    Description=accesslog-processing-simple
    After=syslog.target

    [Service]
    ExecStart=/usr/local/services/accesslog-processing-simple/accesslog-processing-simple.jar
    SuccessExitStatus=143

    [Install]
    WantedBy=multi-user.target
   ```

5. 更新服务配置

   ```
   systemctl daemon-reload
   ```

6. 启动应用程序

   ```
   systemctl start accesslog-processing-simple
   ```

#### accesslog-processing 服务部署

1. 编译应用程序
2. 上传程序文件
   在目录`/usr/local/services/`中创建`accesslog-processing`
目录并将文件包`accesslog-processing-1.0-SNAPSHOT-jar-with-dependencies.jar`、 配置文件`config.properties`以及源码中的`iplib`文件夹拷贝拷进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir /usr/local/services/accesslog-processing
    ```

3. 修改配置项

    ```
    #clickhouse 数据库
    clickhouse.host=localhost:8123
    clickhouse.database=default
    clickhouse.username=default
    clickhouse.password=123456
    
    kafka.bootstrap.server=localhost:9092
    
    # 匹配accesslog的正则表达式
    accesslog-grok=%{IPORHOST:remote_addr} - \[%{DATA:x_forward_for}\] - %{DATA:remote_user} \[%{HTTPDATE:time}\] \"%{WORD:request_method} %{DATA:uri} HTTP/%{NUMBER:http_version}\" %{NUMBER:status} %{NUMBER:body_sent_bytes} \"%{DATA:http_referrer}\" \"%{DATA:http_user_agent}\" %{NUMBER:request_length} %{NUMBER:request_time} \[%{DATA:proxy_upstream_name}\] %{IP:upstream_addr}:%{NUMBER:upstream_addr_port} %{NUMBER:upstream_response_length} %{NUMBER:upstream_response_time} %{NUMBER:upstream_status} %{DATA:req_id} %{DATA:host} \[\]
    kafka.accesslog-topic=accesslog
    kafka.accesslog-group-id=accesslog-group
    flink.accesslog-data-source-name=AccesslogKafkaSource
    
    # flink conf
    flink.checkpoint=file:///usr/local/services/accesslog-processing/checkpoints
    flink.parallelism=1
    processing-file-location=/usr/local/services/accesslog-processing

    # redis
    redis.host=localhost
    redis.port=6379
    redis.password=
    ```

4. 启动应用程序

   ```
   cd /usr/local/services/accesslog-processing /usr/local/services/flink-1.16.3/bin/flink run [-s file: /usr/local/services/accesslog-processing/checkpoints/41f3b324752da77ed7821033d45d1d2f/chk-2737882] -c com.zcunsoft.accesslog.processing.entry.JieXiJson /usr/local/services/accesslog-processing/accesslog-processing-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

   - 其中 -s 参数为checkpoint位置。对于中断后再执行的任务，需要指定该参数，如不指定则从头开始消费kafka消息。

### 4.部署accesslog-api 服务

1. 编译应用程序
2. 上传程序文件
   在目录`/usr/local/services/`中创建`accesslog-api`目录并将文件包`accesslog-api.jar`、配置文件`application.yml`拷贝进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir accesslog-api
    ```

    - 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码

    ```
    chmod 500 accesslog-api.jar
    ```

3. 修改配置文件
   根据前面部署的组件配置，修改`application.yml`中`clickhouse`相关配置，代码如下：

    ```
    springdoc:
    swagger-ui:
        enabled: true
        tagsSorter: alpha
    api-docs:
        path: /accesslog-api/v3/api-docs
        enabled: true
    group-configs:
        - group: 'default'
        paths-to-match: '/**'
        packages-to-scan: com.zcunsoft.accesslog.api.controllers
    server:
    port: 8087
    spring:
    application.name: accesslog-api
    datasource:
    # clickhouse 配置
        clickhouse:
        jdbc-url: jdbc:clickhouse://localhost:8123/accesslogdb
        username: default
        password: 
        driver-class-name: com.clickhouse.jdbc.ClickHouseDriver
        connection-timeout: 20000
        maximum-pool-size: 5
    logging:
    file:
        path: log
    accesslogapi:
    access-control-allow-origin: "*"
    project-name: accesslog
    ```

4. 创建服务

    ```
    vim /etc/systemd/system/accesslog-api.service
    [Unit]
    Description= accesslog-api
    After=syslog.target

    [Service]
    ExecStart=/usr/local/services/accesslog-api /accesslog-api.jar
    SuccessExitStatus=143

    [Install]
    WantedBy=multi-user.target
    ```

5. 启动应用程序

   ```
   systemctl start accesslog-api
   ```

### 5.部署accesslog-ui 服务

1. 在开发环境编译前端应用程序
    本地前端代码运行步骤参考：
    1）部署依赖

    ```
    npm install
    ```

    2）修改相关配置

    ```
    修改接口地址
    ```

2. 打包

    ```
    npm run build 
    ```

3. 部署
    将`dist`目录文件拷贝至web服务器（nginx或者apache或iis）。
4. 登录验证
   此时您可访问http://YOUR_DOMAIN/ ，验证部署成果。 预置账号密码为 admin/clklog。

### 6.nginx 路由配置参考

1. 创建配置文件，设置路由

    ```
    vim /etc/nginx/conf.d/accesslog.conf
    ```

  内容如下，注意替换`YOUR_DOMAIN`为您为监控后台配置的域名：

    ```
    upstream accesslog_api_server {
        server localhost:8087;
    } 
    server {
    listen 80;
    listen [::]:80;
    server_name localhost YOUR_DOMAIN;

    location / {
            root   /usr/share/nginx/html;
            index  index.html index.htm;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /accesslog_api/ {
            proxy_pass http://accesslog_api_server/;
            proxy_set_header Host $host:$server_port;
    }

    location /accesslog_api/v3/ {
            proxy_pass http://accesslog_api_server/accesslog_api/v3/;
            proxy_set_header Host $host:$server_port;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
    ```

2. 重启nginx

    ```
    systemctl restart nginx
    ```
