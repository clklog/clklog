
## 社区版

### 1.准备 linux 服务器

以下配置以 `YOUR_DOMAIN` 指代您使用的域名或ip。

### 2.数据库初始化

1. 解压[docker安装镜像文件],根据镜像目录`clklog_dc_config\init\` 下的`mysql_clklog.sql`文件创建mysql实例schema和表。

```
mysql -u root -p < PATH_TO_SQL_FILE
```

2. 在kafka中创建队列clklog

  ```
  /usr/local/services/kafka_2.12-3.3.1/bin/kafka-topics.sh --create --bootstrap-server 127.0.0.1:9092 --replication-factor 1 --partitions 6 --topic clklog
  ```

<br>

### 3. 部署初始化服务 clklog-init

ClkLog初始化服务，用于clickhouse数据库初始化和定时脚本任务配置。

1. 编译应用程序

2. 上传程序文件
在目录`/usr/local/services/`中创建`clklog-init`目录并将文件包`clklog-init.jar`、配置文件`application.yml`以及源码中的`scripts文件夹`拷贝进去，代码如下：

  ```
  cd /usr/local/services/
  mkdir clklog-init
  
  # 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码
  chmod 500 clklog-init.jar
  ```

3. 修改配置文件，设置$开头的变量
根据前面安装的组件配置，修改`application.yml`中`clickhouse`、`mysql`相关配置，代码如下

```
  server:
    port: 8001
  logging:
    file:
      path: log
  spring:
    application.name: clklog-init
    datasource:
  # clickhouse 配置
      clickhouse:
        jdbc-url: jdbc:clickhouse://$clickhouse_host:8123/default
        username: default
        password: 123456
  init:
    # clklog数据库名
      log-db: clklog
    # 是否启用计划执行
      quartz-enabled: true
    # 各脚本的cron配置，calc-in-order为false时有效
      quartz:
        area_detail_bydate: '0 */1 * * * ?'
        visituri_summary_bydate: '0 */1 * * * ?'
        flow_trend_bydate: '0 */1 * * * ?'
        flow_trend_byhour: '0 */1 * * * ?'
        searchword_detail_bydate: '0 */1 * * * ?'
        channel_detail_bydate: '0 */1 * * * ?'
        device_detail_bydate: '0 */1 * * * ?'
        sourcesite_detail_bydate: '0 */1 * * * ?'
        crashed_detail_bydate: '0 */1 * * * ?'
        user_pv_bydate: '0 */1 * * * ?'
        user_visit_bydate: '0 */1 * * * ?'
        user_visittime_bydate: '0 */1 * * * ?'
        visitor_detail_bydate: '0 */1 * * * ?'
        visitor_life_bydate: '0 */1 * * * ?'
        visituri_detail_bydate: '0 */1 * * * ?'
        visitor_detail_byinfo: '0 */1 * * * ?'
        visitor_detail_bysession: '0 */1 * * * ?'
```

### 4.部署管理接口 clklog-manage

ClkLog管理接口，提供clklog-ui前端项目管理、账号管理及数据清洗过滤配置相关接口。

1. 编译应用程序
2. 上传程序文件
拷贝`clklog-manage.jar`包、源码里的`setting文件夹`和`application.yml`文件至`/usr/local/services/clklog-manage`目录：

  ```
  cd /usr/local/services
  mkdir /usr/local/services/clklog-manage
  chmod 500 clklog-manage.jar
  ```

3. 修改配置文件，设置$开头的变量
根据前面安装的组件配置，修改`application.yml`中的相关配置，代码如下：

  ```
  springdoc:
    swagger-ui:
      enabled: true
      tagsSorter: alpha
    api-docs:
      path: /api/manage/v3/api-docs
      enabled: true
    group-configs:
      - group: 'default'
        paths-to-match: '/**'
        packages-to-scan: com.zcunsoft.clklog.manage.controllers, com.zcunsoft.clklog.sysmgmt.controllers
  server:
    port: 8091
  spring:
    application.name: clklog-manage
    datasource:
      mysql:
        jdbc-url: jdbc:mysql://$mysql_host:3306/clklog?characterEncoding=UTF-8&useTimezone=true&serverTimezone=GMT%2B8
        username: $mysql_user
        password: $mysql_pass
        driver-class-name: com.mysql.cj.jdbc.Driver
      clickhouse:
        jdbc-url: jdbc:clickhouse://$clickhouse_host:8123/clklog
        username: $clickhouse_user
        password: $clickhouse_pass
        driver-class-name: com.clickhouse.jdbc.ClickHouseDriver
    jpa:
      show-sql: false

    redis:
      # 单机配置
      host: $redis_host
      port: 6379
      password: $redis_pass
    # 哨兵配置
    # sentinel:
    #  master: gct
    #  nodes: $node1:26379,$node2:26379,$node3:26379
  token:
    # 令牌自定义标识
    header: Authorization
    # 令牌密钥
    secret: c609737e578978eccc64cef4be680
    # 令牌有效期（默认30分钟）
    expireTime: 300
  logging:
    file:
      path: log
  clklog-common:
    access-control-allow-origin-patterns: "*"
    anonymous-method-list: /auth/login
  clklog-manage:
    log-store-path:
  ```

### 5. 部署统计接口 clklog-api

ClkLog统计接口，提供clklog-ui前端各维度数据查询统计分析接口。

1. 编译应用程序

2. 上传程序文件

   拷贝`clklog-api.jar`包和`application.yml`文件至`/usr/local/services/clklog-api`目录：

    ```
    cd /usr/local/services
    mkdir /usr/local/services/clklog-api
    chmod 500 clklog-api.jar
    ```

3. 修改配置文件

    根据前面安装的组件配置，修改`application.yml`中的相关配置，代码如下：

    ```
    springdoc:
      swagger-ui:
        enabled: true
        tagsSorter: alpha
      api-docs:
        path: /api/v3/api-docs
        enabled: true
      group-configs:
        - group: 'default'
          paths-to-match: '/**'
          packages-to-scan: com.zcunsoft.clklog.api.controllers
    server:
      port: 8087
    spring:
      application.name: clklog-api
      datasource:
        clickhouse:
          jdbc-url: jdbc:clickhouse://localhost:8123/clklog
          username: default
          password: 123456
          driver-class-name: com.clickhouse.jdbc.ClickHouseDriver
          connection-timeout: 20000
          maximum-pool-size: 5
      redis:
        host: 10.10.220.37
        port: 6379
        database: 0
        password:
    token:
      header: Authorization
      secret: c609737e578978eccc64cef4be680
      expireTime: 300
    logging:
      file:
        path: log
    clklog-common:
      access-control-allow-origin: "*"
    clklog-api:
      # 默认前端埋点project默认名称，一般不用修改
      project-name: clklogapp    
    ```

4. 创建服务

    ```
    vim /etc/systemd/system/clklog-api.service
    ```

    内容如下：

    ```
    [Unit]
    Description=clklog-api
    After=syslog.target

    [Service]
    ExecStart=/usr/local/services/clklog-api/clklog-api.jar
    SuccessExitStatus=143

    [Install]
    WantedBy=multi-user.target
    ```

    更新服务

    ```
    systemctl daemon-reload
    ```

5. 启动应用程序

    ```
    systemctl start clklog-api
    ```

<br>

### 6. 统计后台前端展示 clklog-ui

ClkLog前端项目，该项目是基于 vue-element-admin 实现的ClkLog相关统计分析及系统相关功能配置的前端应用。

1. 开发环境编译前端应用程序

    本地前端代码运行步骤参考：

    1）安装依赖

    ```
    npm install
    ```

    2）修改相关配置

    ```
    # 本地环境接口服务代理配置修改
    # 修改vue.config.js 文件中的代理配置 
    '/DEV-API'的'target'为clklog-api接口的代理地址 'http://YOUR_DOMAIN/api'
    

    # 生产环境接口服务代理配置修改
    # 方式一、先修改public/config.js 文件中配置，再发布
    
        BASE_API: "http://YOUR_DOMAIN/api" //clklog-api的接口的代理地址
        
        npm run build

    # 方式二、发布后修改代理配置
        npm run build 

        dist/config.js
        BASE_API: "http://YOUR_DOMAIN/api" //clklog-api的接口的代理地址
    ```

    3）启动服务

    ```
    npm run dev
    ```

    4）浏览器访问

    接入埋点代码后，当统计后台前端展示页面能够正常显示统计相关数据，说明整个流程正常运行。

2. 打包

    ```
    npm run build
    ```

3. 部署

   将`dist`目录文件拷贝至web服务器（nginx或者apache或iis）。

4. 登录验证
   此时您可访问http://YOUR_DOMAIN/ ，验证部署成果。
   预置账号密码为 admin/clklog。

### 7.部署接收服务 clklog-receiver

ClkLog数据接收服务，用于接收客户端项目通过神策SDK埋点后采集的日志数据并存入kafka。

1. 编译应用程序

2. 上传程序文件

    在目录`/usr/local/services/`中创建`clklog-receiver`目录并将文件包`clklog-receiver.jar`、配置文件`application.yml`以及源码中的`iplib`文件夹和`project-setting.json`文件拷贝进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir clklog-receiver

    # 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码
    chmod 500 clklog-receiver.jar
    ```

3. 修改配置文件

    根据前面安装的组件配置，修改`application.yml`中`redis`、`kafka`相关配置，代码如下：

    ```
    spring:
      kafka:
        # bootstrap-servers的值根据kafka配置文件里的listeners的值进行配置。
        bootstrap-servers: localhost:9092
      redis:
        # 单机配置
        host: localhost
        port: 6379
        # password: 
        # 哨兵配置
        # sentinel:
        # master: gct
        # nodes: 10.100.2.1:26379,10.100.2.2:26379,10.100.2.3:26379
   kafka:

  bootstrap-servers: 10.10.220.188:9092
  producer: # 生产者
    client-id: "clklog-producer-group"
    retries: 3 # 设置大于 0 的值，则客户端会将发送失败的记录重新发送
    topic: clklog
    acks: 0
    key-serializer: org.apache.kafka.common.serialization.StringSerializer
    value-serializer: org.apache.kafka.common.serialization.StringSerializer
    properties:
   linger:
     ms: 1000
  listener:
    ack-mode: MANUAL_IMMEDIATE
    receiver:
      # resource-path 默认为空，如果资源及配置文件(iplib,app-setting.json)不与jar同目录，则修改为它们的父路径，否则无需配置。
      resource-path:  
      # enable-simple-version 默认为false, 表示日志存入kafka，由flink处理后存入clickhouse；当值为true时，表示日志直接存入clickhouse，无需安装kafka和flink。配置修改后需重启clklog-receiver服务。
      enable-simple-version: false
      access-control-allow-origin-patterns: "*"

    ```

4. 创建服务

    ```
    vim /etc/systemd/system/clklog-receiver.service
    ```

    内容如下：

    ```
    [Unit]
    Description=clklog-receiver
    After=syslog.target

    [Service]
    ExecStart=/usr/local/services/clklog-receiver/clklog-receiver.jar
    SuccessExitStatus=143

    [Install]
    WantedBy=multi-user.target
    ```

    更新服务配置

    ```
    systemctl daemon-reload
    ```

5. 启动应用程序

    ```
    systemctl start clklog-receiver
    ```

### 8.部署处理服务 clklog-processing

ClkLog数据处理服务，依托flink，消费kafka数据并存入clickhouse。

1. 编译应用程序

2. 上传程序文件

    在目录`/usr/local/services/`中创建`clklog-processing`目录并将文件包`clklog-processing-jar-with-dependencies.jar`、 配置文件`config.properties`以及源码中的`iplib`文件夹和`project-setting.json`文件拷贝拷进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir /usr/local/services/clklog-processing
   
    # 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码
    chmod 500  clklog-processing-jar-with-dependencies.jar
    ```

3. 修改配置项

   ```

    vim config.properties

    # clickhouse 数据库连接配置
    clickhouse.host=localhost:8123
    clickhouse.database=clklog
    clickhouse.username=default
    clickhouse.password=123456

    # kafka 连接配置
    kafka.bootstrap.server=localhost:9092
    kafka.clklog-topic=clklog
    kafka.clklog-group-id=clklog-group
    
    # flink 配置
    flink.clklog-data-source-name=ClklogKafkaSource
    flink.checkpoint=file:///usr/local/services/clklog-processing/checkpoints
    flink.parallelism=1
    
    # processing-file-location 默认为空，如果资源及配置文件(iplib,app-setting.json)不与jar同目录，则修改为它们的父路径，否则无需配置
    processing-file-location=
    
    # redis 配置
    redis.host=localhost
    redis.port=6379
    redis.password=
    redis.database=0
    redis.pool.max-active=3
    redis.pool.max-idle=3
    redis.pool.min-idle=0
    redis.pool.max-wait=-1
    ```

4. 启动应用程序

    ```
    cd /usr/local/services/clklog-processing

   /usr/local/services/flink-1.14.6/bin/flink run [-s file: /usr/local/services/clklog-processing/checkpoints/41f3b324752da77ed7821033d45d1d2f/chk-2737882] -c com.zcunsoft.clklog.analysis.entry.JieXiJson /usr/local/services/clklog-processing/clklog-processing-jar-with-dependencies.jar
    ```

    其中 `-s` 参数为`checkpoint`位置。对于中断后再执行的任务，需要指定该参数，如不指定则从头开始消费`kafka`消息。

### 9. nginx 路由配置参考

1. 确认您使用的nginx捆绑了with-http_auth_request_module模组

```
# 执行如下命令，确认输出包含 --with-http_auth_request_module

nginx -V 2>&1 | grep -- 'http_auth_request_module'

# 如果没有，请重新安装nginx

```

2. 创建配置文件，设置路由

    ```
    vim /etc/nginx/conf.d/clklog.conf
    ```

    内容如下，注意替换`YOUR_DOMAIN`为您为监控后台配置的域名：

    ```
    upstream clklog_api_server {
            server localhost:8087;
        }

    upstream clklog_receiver_server {
            server localhost:8002;
        }

    upstream clklog_flink_server{
            server localhost:8081;
    }
    upstream clklog_manage_server{
            server localhost:8091;
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

    location /receiver/ {
                proxy_pass http://clklog_receiver_server/;
                proxy_set_header Host $host:$server_port;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            }
    location /api/manage/ {
            proxy_pass http://clklog_manage_server/;
            proxy_set_header Host $host:$server_port;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

    location /api/manage/v3/ {
            proxy_pass http://clklog_manage_server/manage/v3/;
            proxy_set_header Host $host:$server_port;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

    location /api/ {
                proxy_pass http://clklog_api_server/;
                proxy_set_header Host $host:$server_port;
            }

    location /api/v3/ {
                proxy_pass http://clklog_api_server/api/v3/;
                proxy_set_header Host $host:$server_port;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            }
    #因安全问题，建议限制flink后台访问
    location /clklog_flink/ {
                proxy_pass http://clklog_flink_server/;
                proxy_set_header Host $host:$server_port;
            }

    }

    ```

3. 重启nginx

    ```
    systemctl restart nginx
    ```

### 10. 部署环境验证

1. Flink后台地址

    ```
    http://YOUR_DOMAIN:8081/
    ```

2. 日志接收地址

    ```
    http://YOUR_DOMAIN/receiver/api/gp
    ```

3. 接口地址

    ```
    统计接口说明地址
    http://YOUR_DOMAIN/api/doc.html#/home

    管理接口说明地址
    http://YOUR_DOMAIN/api/manage/doc.html#/home

    ```

4. 前端地址
   clklog-ui用户名密码为 admin/clklog

    ```
    http://YOUR_DOMAIN/
    ```

### 10. sdk 埋点集成

   埋点代码接入方式参考：[sdk-埋点集成参考](/integration/reference.md)

## 付费版

   购买付费版后，提供详细安装部署说明文档。
