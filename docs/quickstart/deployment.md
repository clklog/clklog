
## 1.准备 linux 服务器

以下配置以 `YOUR_DOMAIN` 指代您使用的域名或ip。

## 2.数据库初始化

1. 根据 `scripts` 下的`sql脚本.txt`文件创建clickhouse实例schema和表

  ```
  clickhouse-client --host localhost --user default --queries-file PATH_TO_SQL_FILE
  ```

2. 在kafka中创建队列clklog

  ```
  /usr/local/services/kafka_2.12-3.3.1/bin/kafka-topics.sh --create --bootstrap-server 127.0.0.1:9092 --replication-factor 1 --partitions 6 --topic clklog
  ```

<br>

## 3.nginx 路由配置参考

1. 创建配置文件，设置路由

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

    location /clklog_receiver/ {
                proxy_pass http://clklog_receiver_server/;
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

    location /clklog_flink/ {
                proxy_pass http://clklog_flink_server/;
                proxy_set_header Host $host:$server_port;
            }

    }
    ```

2. 重启nginx

    ```
    systemctl restart nginx
    ```

## 4. 部署统计接口 clklog-api

1. 编译应用程序

2. 上传程序文件

   拷贝`clklog-api-1.0.0.jar`包和`application.yml`文件至`/usr/local/services/clklogapi`目录：

    ```
    cd /usr/local/services
    mkdir /usr/local/services/clklogapi
    chmod 500 clklog-api-1.0.0.jar
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
    logging:
      file:
        path: log
    clklogapi:
      access-control-allow-origin: "*"
      # 默认前端埋点project默认名称，一般不用修改
      project-name: clklogapp    
      # 埋点网站域名配置，多个域名以英文逗号分隔
      project-host: http(s)://{{hostname}}
    ```

4. 创建服务

    ```
    vim /etc/systemd/system/clklogapi.service
    ```

    内容如下：

    ```
    [Unit]
    Description=clklog-api
    After=syslog.target

    [Service]
    ExecStart=/usr/local/services/clklogapi/clklog-api-1.0.0.jar
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
    systemctl start clklogapi
    ```

<br>

## 5. 统计后台前端展示 clklog-ui

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

## 6.部署接收服务 clklog-receiver

1. 编译应用程序

2. 上传程序文件

    在目录`/usr/local/services/`中创建`clklogreceiver`目录并将文件包`clklog_receiver.jar`、配置文件`application.yml`以及源码中的`iplib`文件夹和`app-setting.json`文件拷贝进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir clklogreceiver

    # 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码
    chmod 500 clklog_receiver.jar
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
    receiver: 
      # 对应前端埋点代码配置的project名称，多个project以逗号分隔。
      project-list: clklogapp
      # resource-path 默认为空，如果资源及配置文件(iplib,app-setting.json)不与jar同目录，则修改为它们的父路径，否则无需配置。
      resource-path:  
      # enable-simple-version 默认为false, 表示日志存入kafka，由flink处理后存入clickhouse；当值为true时，表示日志直接存入clickhouse，无需安装kafka和flink。配置修改后需重启clklog-receiver服务。
      enable-simple-version: false
      access-control-allow-origin-patterns: "*"
     
    ```

4. 创建服务

    ```
    vim /etc/systemd/system/clklogreceiver.service
    ```

    内容如下：

    ```
    [Unit]
    Description=clklogreceiver
    After=syslog.target

    [Service]
    ExecStart=/usr/local/services/clklogreceiver/clklog_receiver-1.0.0.jar
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
    systemctl start clklogreceiver
    ```

## 7.部署处理服务 clklog-processing

1. 编译应用程序

2. 上传程序文件

    在目录`/usr/local/services/`中创建`clklogprocessing`目录并将文件包`clklog-processing-1.0.0-jar-with-dependencies.jar`、 配置文件`config.properties`以及源码中的`iplib`文件夹和`project-setting.json`文件拷贝拷进去，代码如下：

    ```
    cd /usr/local/services/
    mkdir /usr/local/services/clklogprocessing
   
    # 为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码
    chmod 500  clklog-processing-1.0.0-jar-with-dependencies.jar
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
    flink.checkpoint=file:///usr/local/services/clklogprocessing/checkpoints
    flink.parallelism=1
    
    # processing-file-location 默认为空，如果资源及配置文件(iplib,app-setting.json)不与jar同目录，则修改为它们的父路径，否则无需配置
    processing-file-location=
    
    # redis 配置
    redis.host=localhost
    redis.port=6379
    redis.password=
    ```

4. 启动应用程序

    ```
    cd /usr/local/services/clklogprocessing

   /usr/local/services/flink-1.14.6/bin/flink run [-s file: /usr/local/services/clklogprocessing/checkpoints/41f3b324752da77ed7821033d45d1d2f/chk-2737882] -c com.zcunsoft.clklog.analysis.entry.JieXiJson /usr/local/services/clklogprocessing/clklog-processing-1.0.0-jar-with-dependencies.jar
    ```

    其中 `-s` 参数为`checkpoint`位置。对于中断后再执行的任务，需要指定该参数，如不指定则从头开始消费`kafka`消息。

## 8. 部署计算脚本clklog-scripts

- 计算脚本是基于Clickhouse集群中`log_analysis`表进行多维计算，获得各个维度上的统计报表。
  使用Cron来进行任务的定时调度。

1. 创建脚本及日志存放目录

    ```
    mkdir /usr/local/services/scripts
    mkdir /usr/local/services/scripts/sh
    mkdir /usr/local/services/scripts/flock
    mkdir /usr/local/services/scripts/crontab_log
    mkdir /usr/local/services/scripts/clklog
    ```

2. 上传sh计算脚本

   - **注意：上传脚本时，需设置迁移类型为ASCII(I)**
  
    脚本上传于`/usr/local/services/scripts/sh`目录,  为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码：

    ```
    chmod 500 *.sh
    ```

1. 修改脚本中的数据库链接配置

    根据clickhouse配置，修改脚本目录中`clklog-scripts.env`文件的clickhouse的`用户名、密码`配置信息：

    ````
    # [Clickhouse]
    CK_USER_NAME=default
    CK_USER_PWD=123456

    # [ClkLog]
    CLKLOG_LOG_DB=clklog
    CLKLOG_STAT_DB=clklog
    CLKLOG_SCRIPT_LOG=/usr/local/services/scripts/clklog
    ````

2. 设置调度任务

    ```
    crontab -e
    ```

    将 `scripts`目录下的 `定时脚本.txt`文件,内容复制过去`.wq`保存。

    备注：脚本的日志在`/usr/local/services/scripts/clklog/`
<br>

## 9. 部署环境验证

1. Flink后台地址

    ```
    http://YOUR_DOMAIN:8081/
    ```

2. 日志接收地址

    ```
    http://YOUR_DOMAIN/clklog_receiver/api/gp
    ```

3. 接口地址

    ```
    统计接口说明地址
    http://YOUR_DOMAIN/api/doc.html#/home
    ```

4. 前端地址
   clklog-ui用户名密码为 admin/clklog

    ```
    http://YOUR_DOMAIN/
    ```

## 11. sdk 埋点集成

### 1. Web JS 埋点集成参考

1.1. 下载 ClKLOG WEB JS SDK

  <a href="https://clklog.com/res/clklog.webjs.sdk.v2.zip" target="_blank" rel="noopener" id="webjssdkdownload">点击此处下载 CLKLOG WEB JS SDK</a>, 并将下载的`ClKLOG WEB JS SDK`文件包解压至网站根目录, 目录结构如下：

  ```
  ├── 网站根目录
      ├── plugins
      │   ├── session-event
      │   │   ├── index.js
      |── sensorsdata.js
      |── autotrack.js
  ```

1.2. 修改`autotrack.js`接收服务配置信息

  将`autotrack.js`中的`server_url` 接收服务地址配置信息修改为clklog数据采集地址，并调整参数`project`和`token`的配置。

  其中`project`名称默认为`clklogapp`，如果要修改`project`名称请注意调整`clklog-receiver`服务中`project-list`的相关配置。

  `autotrack.js`中的`server_url`参考配置如下：

  ```
    //接收地址为clklog_receiver 的接收服务地址，project和token参数必须传入，token是每个project对应的随机字符串，请自行随机生成。
    server_url: 'http://10.10.222.21/clklog_receiver/api/gp?project=clklogapp&token=5388ed7459ba4c4cad0c8693fb85630a', 
  ```

- **单页面应用数据采集说明**

  1）如果是单页面应用，标题不变但需要自动采集页面浏览事件，需要将`autotrack.js`中的`is_track_single_page`值设置为`ture`。

  2）如果是单页面应用，标题会随着页面变化，同时也需要采集页面浏览事件，需要将`autotrack.js` 中的`is_track_single_page` 值设置为`false`，同时在页面标题改变结束后执行代码：  `sensors.quick('autoTrackSinglePage');`

1.3. 接入埋点跟踪代码

   在web网站引用`autotrack.js`。

1.4. 测试埋点代码是否接入正常

  在本地启动网站，打开浏览器访问网站，打开开发者工具，查看控制台, 出现如下提示信息说明埋点代码接入成功。

  ![](../assets/imgs/autotrack.png)

- **locahost或ip访问时可忽略控制台中的 “[web-sdk-log]: getHostname传入的url参数不合法！” 提示**

### 2. Android SDK埋点集成

  集成方式参考 [神策Android SDK集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/android-7541696.html) , 集成时将数据接收地址更换成clklog_receiver的接收服务地址。

### 3. IOS SDK埋点集成

  集成方式参考 [神策IOS SDK集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/ios-7538614.html) , 集成时将数据接收地址更换成clklog_receiver的接收服务地址。

### 4. 小程序 SDK埋点集成

  集成方式参考 [神策微信小程序 SDK集成文档](https://manual.sensorsdata.cn/sa/latest/zh_cn/%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3%EF%BC%88%E5%BE%AE%E4%BF%A1%E5%B0%8F%E7%A8%8B%E5%BA%8F%EF%BC%89-1573892.html) , 集成时将数据接收地址更换成clklog_receiver的接收服务地址。
