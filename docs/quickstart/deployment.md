
# 应用部署

## 1.准备 linux 服务器

假设IP 为 `10.10.222.21`

## 2.数据库初始化

<!-- > 下载源码：
<a href="https://github.com/clklog/clklog-scripts" target="_blank">[GitHub 下载]</a> -->

1. 在clickhouse里创建数据库`clklog`

    ```
    CREATE DATABASE clklog ENGINE = Atomic
    ```

2. 根据 `scripts` 下的`sql脚本.txt`文件创建表

## 3.部署接收服务 clklog-receiver

<!-- > 下载源码：
<a href="https://github.com/clklog/clklog-receiver" target="_blank">[GitHub 下载]</a> -->

1. 编译应用程序

2. 上传程序文件

    将`clklog_receiver.jar`包和 `application.yml`文件拷贝至`/usr/local/services/`目录：

    ```
    cd /usr/local/services/
    mkdir clklogreceiver
    chmod 500 clklog_receiver.jar
    ```

3. 修改配置文件

    根据前面安装的组件配置，修改`application.yml`中`redis`、`kafka`相关配置，代码如下：

    ```
    server: 
      tomcat: 
        #日志配配置
        accesslog: 
          #目录
          directory: /usr/local/services/receiverlogs      
    logging:
      file:
        path: log
    spring:
      kafka:
        bootstrap-servers: localhost:9092
      redis:
        host: localhost
        port: 6379
    receiver: 
      app-list: clklogapp
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

## 4.部署处理服务 clklog-processing

<!-- > 下载源码：
<a href="https://github.com/clklog/clklog-processing" target="_blank">[GitHub 下载]</a> -->

1. 编译应用程序

2. 上传程序文件

   拷贝`clklog-processing.jar`包至`/usr/local/services/clklogprocessing`目录

    ```
    cd /usr/local/services/
    mkdir /usr/local/services/clklogprocessing
    chmod 500  clklog-processing.jar 
    ```

3. 启动应用程序

    ```
    /usr/local/services/flink-1.14.6/bin/flink run -s file: /usr/local/services/clklogprocessing/checkpoints/41f3b324752da77ed7821033d45d1d2f/chk-2737882  -c com.zcunsoft.clklog.analysis.entry.JieXiJson /usr/local/services/clklogprocessing/clklog-processing.jar
    ```

    其中 `-s` 参数为`checkpoint`位置。对于中断后再执行的任务，需要指定该参数，如不指定则从头开始消费`kafka`消息。

## 5. 部署计算脚本clklog-script

<!-- > 下载脚本: <a href="https://github.com/clklog/clklog-scripts" target="_blank">[GitHub 下载]</a> -->

- 计算脚本是基于Clickhouse集群中的`log_analysis`表进行多维计算，获得各个维度上的统计报表。
    使用Cron来进行任务的定时调度。

1. 创建脚本及日志存放目录

    ```
    mkdir /usr/local/services/scripts
    mkdir /usr/local/services/scripts/sh
    mkdir /usr/local/services/scripts/flock
    mkdir /usr/local/services/scripts/crontab_log
    mkdir /usr/local/services/scripts/cklog
    ```

2. 上传sh计算脚本

   - **注意：上传脚本时，需设置迁移类型为ASCII(I)**
  
    脚本上传于`/usr/local/services/scripts/sh`目录,  为防止出现权限问题导致脚本不能执行，建议上传完脚本以后执行以下代码：

    ```
    chmod 500 *.sh
    ```

1. 修改脚本中的数据库链接配置

    根据clickhouse配置，修改脚本中连接clickhouse的`用户名、密码`配置信息：

    ````
    ck_login="clickhouse-client -u default --password 123456"
    ck_log_db="clklog."
    ck_stat_db="clklog."
    ````

2. 设置调度任务

    ```
    crontab -e
    ```

    将 `scripts`目录下的 `定时脚本.txt`文件,内容复制过去`.wq`保存。

    备注：脚本的日志在`/usr/local/services/scripts/cklog/`
<br>

## 6. 部署统计接口 clklog-api

<!-- > 下载源码：
<a href="https://github.com/clklog/clklog-api" target="_blank">[GitHub 下载]</a> -->

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
        path: /clklog_api/v3/api-docs
        enabled: true
      group-configs:
        - group: 'default'
          paths-to-match: '/**'
          packages-to-scan: com.zcunsoft.tracking.api.controllers
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
    trackingapi:
      access-control-allow-origin: "*"
      project-name: clklogapp
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

## 7. nginx 路由设置参考

1. 创建配置文件，设置路由

    ```
    vim /etc/nginx/conf.d/clklog.conf
    ```

    内容如下：

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
        server_name localhost;

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

    location /clklog_api/ {
                proxy_pass http://clklog_api_server/; 
                proxy_set_header Host $host:$server_port; 
            }

    location /clklog_api/v3/ {
                proxy_pass http://clklog_api_server/clklog_api/v3/; 
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

## 8. 部署环境验证

1. Flink后台地址

    ```
    http://10.10.222.21/clklog_flink/#/overview
    ```

2. 日志接收地址

    ```
    http://10.10.222.21/clklog_receiver/api/gp
    ```

3. 接口地址

    ```
    http://10.10.222.21/clklog_api/doc.html#/home
    ```

## 9. sdk 埋点参考

#### Web JS 埋点参考

1. 下载神策 WEB JS SDK

    [下载 sensorsdata.js](https://github.com/sensorsdata/sa-sdk-javascript/blob/v1.25.15/dist/web/sensorsdata.js)

    [下载插件 exposure](https://github.com/sensorsdata/sa-sdk-javascript/blob/v1.25.15/dist/web/plugin/exposure/index.js)

    [下载插件 session-event](https://github.com/sensorsdata/sa-sdk-javascript/blob/v1.25.15/dist/web/plugin/session-event/index.js)

2. 引用插件

    插件引用按目录结构参考如下：

    ```
      dist
    ├── web
        ├── plugin
        │   ├── exposure
        │   │   ├── index.js
        │   ├── session-event
        │   │   ├── index.js
        |── sensorsdata.js
    ```

3. 接入代码

    创建文件`autotrack.js`，然后在web网站引用即可，代码如下：

    ```
    (function (para) {
      var p = para.sdk_url, n = para.name, w = window, d = document, s = 'script', x = null, y = null;
      if (typeof (w['sensorsDataAnalytic201505']) !== 'undefined') {
        return false;
      }
      w['sensorsDataAnalytic201505'] = n;
      w[n] = w[n] || function (a) { return function () { (w[n]._q = w[n]._q || []).push([a, arguments]); } };
      var ifs = ['track', 'quick', 'register', 'registerPage', 'registerOnce', 'trackSignup', 'trackAbtest', 'setProfile', 'setOnceProfile', 'appendProfile', 'incrementProfile', 'deleteProfile', 'unsetProfile', 'identify', 'login', 'logout', 'trackLink', 'clearAllRegister', 'getAppStatus'];
      for (var i = 0; i < ifs.length; i++) {
        w[n][ifs[i]] = w[n].call(null, ifs[i]);
      }
      if (!w[n]._t) {
        x = d.createElement(s), y = d.getElementsByTagName(s)[0];
        x.async = 1;
        x.src = 'plugin/session-event/index.js';//引用的session-event插件路径
        x.setAttribute('charset', 'UTF-8');
        w[n].para = para;
        y.parentNode.insertBefore(x, y);
        x = d.createElement(s), y = d.getElementsByTagName(s)[0];
        x.async = 1;
        x.src = 'plugin/exposure/index.js';//引用的exposure插件路径
        x.setAttribute('charset', 'UTF-8');
        w[n].para = para;
        y.parentNode.insertBefore(x, y);
        x = d.createElement(s), y = d.getElementsByTagName(s)[0];
        x.async = 1;
        x.src = p;
        x.setAttribute('charset', 'UTF-8');
        w[n].para = para;
        y.parentNode.insertBefore(x, y);
      }
      sensors.quick("isReady", function () {
        sensors.use('Exposure', {
          area_rate: 1,
          stay_duration: 2,
          repeated: false
        });
        sensors.use('PageLeave', { heartbeat_interval_time: 5 });
        sensors.use('PageLoad');
        sensors.use('SessionEvent');
      });
      sensors.quick("autoTrackSinglePage")
    })({
      sdk_url: 'sensorsdata.js',
      name: 'sensors',
      show_log: true,
      // send_type:'beacon',
      server_url: 'http://10.10.222.21/clklog_receiver/api/gp?project=clklogapp&token=gfdsg325432gfsgfds', //clklog_receiver 的接收服务地址
      heatmap: {
        clickmap: 'default', scroll_notice_map: 'default', collect_tags: {
          div: true,
          img: true
        }
      },
      preset_properties: { latest_referrer_host: true }
    });
    ```

4. 发布WEB站点到服务器
5. 访问站点

## 10. 统计后台前端展示 clklog-ui

1. 安装依赖

    ```
    npm install
    ```
  
2. 启动服务

    ```
    npm run dev
    ```

3. 浏览器访问

    当统计后台前端展示页面能够正常显示统计相关数据，说明整个流程正常运行。