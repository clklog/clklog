
# Docker安装运维技术指南

## 常见问题

> Q: ClkLog可以私有化部署吗？

- A: 可以。
<br><br>

> Q: 没有Java开发环境如何使用ClkLog？

- 可以使用Docker安装。[Docker安装参考](/docker_installation/introduce.md)。
<br><br>

> Q: 通过docker-compose部署，是不是改不了源码了？

- 是的。如果要二开，建议源码部署。
<br><br>

> Q: Docker部署，可以部署在windows服务器上吗？

- A：不可以，Docker部署支持在Linux服务器上，建议操作系统版本为Ubuntu 22.04。
<br><br>

> Q: Docker容器部署支持部署在arm架构上吗？

- A：不支持。
<br><br>

> Q：使用Docker安装，可以使用已有的MySQL、Clickhouse、Kafka、Redis等组件吗？

- A：可以使用外部组件，修改docker-compose文件中的相关组件的配置即可。
<br><br>

> Q： docker-compose中的组件端口可以修改吗？

- A：可以，compose中的组件端口修改后需同步修改nginx配置。
<br><br>

> Q：Docker安装，数据库密码可以修改吗？  

- A： 可以。[修改步骤参考](/docker_installation/tutorials.md#数据库相关配置)
 <br><br>

## 常用命令

- 查看容器状态

    ```
    docker ps -a 
    ```

- 查看内存使用情况

  ```
  docker stats
  ```

- 查看容器日志

  ```
  docker logs <容器ID>
  ```

- 导出容器日志

  ```
  docker logs <容器ID> > <日志路径>
  ```

## 异常日志所在位置

### clklog-init

- 日志在`yml`编排文件同目录的 `clklog_dc_data/initlog/`下。

#### clklog-receiver

- 日志在`yml`编排文件同目录的 `clklog_dc_data/receiver/`下。

### processing

在flink后台查看异常日志：

- job manager->log list
- task-manager -> <path,id> -> log list

### clklog-api

- 日志在对应容器的`/log`下。

### clklog-manage

- 日志在对应容器的`/log`下。

### flink

- 日志在对应容器的`/opt/flink/log`下。

## 数据库相关配置

### MySql

- **原始密码**

  - 配置在`yml`编排文件同目录的下`.env`文件中的`MYSQL_ROOT_PASSWORD`中。

### Clickhouse

- **原始密码**

  - 配置在`yml`编排文件同目录的下`.env`文件中的`CK_USER_PWD`中。

- **如何修改密码**

  - 步骤一：修改`.env`文件里的`CK_USER_PWD`的数据库密码。

  - 步骤二：修改容器中`clickhouse`的`password_double_sha1_hex`节点值。

    执行以下脚本，获取 ”新密码” 的SHA1哈希值

    ```
    echo -n "新密码" | sha1sum | awk '{print $1}' | xxd -r -p | sha1sum | awk '{print $1}'
    ```

    将上述脚本输出的SHA1哈希值更新至`clklog_dc_config/clickhouse/users.d/default-password.xml`里`password_double_sha1_hex`节点， 重启`clickhouse`生效.

- **系统日志TTL配置**
  - 系统日志TTL配置用于自动删除旧数据。
  - 根据实际运营需求，可以通过修改`./clklog_dc_config/clickhouse/config.d/default-config.xml`里对应节点配置，调整`query_log`、`trace_log`等系统日志表的TTL配置。

## 容器启动可能的异常解决方法

### Kafka

- 异常原因：**mkdir: cannot create directory '/bitnami/kafka/config': Permission denied**
- 解决方法：确认`clklog_init.sh`脚本执行且成功。

### Zookeeper

- 异常原因：**mkdir: cannot create directory '/bitnami/zookeeper/data': Permission denied**
- 解决方法：确认`clklog_init.sh`脚本执行且成功。

### Redis

- 异常原因：**无权限 (Permission denied)**
- 解决方法：确认`clklog_init.sh`脚本执行且成功。

### Mysql

- 异常原因：**Fatal glibc error: CPU does not support x86-64-v2**
- 解决方法：将`docker-compose-clklog-XXX-base.yml`编排文件中的`mysql`镜像地址改成`registry.cn-shanghai.aliyuncs.com/clklog/mysql:8.4.0-oraclelinux8`

### Clickhouse

- 异常原因：**clklog数据库及统计表创建失败**
- 解决方法：
  - 检查`clklog-init`容器的状态是否正常。
  - 检查`docker-compose-clklog-XXX.yml`编排文件`clklog-init`节点`INIT_MYSQL_DB`配置项是否正确。

## 相关服务可能出现的异常排查方法

### clklog-receiver

- **接收不到数据**

  - 检查客户端是否正常配置`receiver`数据接收地址。
  - 确认接收地址中的`project`是否已经在后台项目管理里注册。如果刚注册，过30秒后，再尝试提交数据。
  - 检查`receiver-server.log`是否有异常信息
  
>**receiver接收的日志说明**
>
> - `receiver`接收的原始日志会以文本文件的形式，以项目编码为目录存储于`clklog_dc_data/receiverlogs`下。
> - 日志文件`store.log`按天进行数据归档。
> - 在实际运营中，可根据实际情况备份或删除`receiver`接收的原始日志文本文件。
>
> **注意**
>
> - 标准模式下，只要`receiver`能正常接收并将数据存储至`receiverlogs`中，则其他服务的异常停止不会造成数据丢失，查找并处理相关问题后，重启相关服务，系统会继续处理未入库的数据。
> - 快速模式下，如果`receiver`能正常接收并将数据存储至`receiverlogs`中，但`clickhouse`数据库出现问题，重启相关服务，未入库的数据不会重新入库，只会存在于`receiverlogs`中。

### clklog-processing

- **1. 内存溢出导致容器宕机**
  - 删除2个`processing`容器(`taskmanager`和`jobmananger`)。
  - 调整编排文件`docker-compose-clklog-full.yml`中`taskmanager`节点`taskmanager.memory.process.size`的配置。
  - 重启容器。

- **2.异常：Could not acquire the minimum required resources**

  - 删除2个`processing`容器(`taskmanager`和`jobmananger`)。
  - 重启容器。

- **3. 异常：Exceeded checkpoint tolerable failure threshold**

  - 在`processing`的配置文件`/clklog_dc_config/processing/config.properties`增加配置`flink.checkpoint.timeout=300000`。
  - 删除2个`processing`容器。
  - 重启容器。

- **4. 处理慢**
  - 调整`processing`的配置文件`/clklog_dc_config/processing/config.properties`中的`flink.parallelism`配置。
    - 注意：配置的值过大会增大内存消耗, 需要相应调整编排文件`docker-compose-clklog-full.yml`中`taskmanager`节点`taskmanager.memory.process.size`的配置。
  - 重启容器。

### clklog-ui

- **1. 无法登录**

  - 检查`clklog-manage`容器的状态及日志。

- **2. 所有前端页面报错502**

  - 等待容器启动完成，大约3分钟。

- **3. 事件分析/数据统计的接口502【付费版】**

  - 检查`clickhouse`数据库中里`mysql_tbl_*`相关表是否能正常访问，如果不能正常访问，则检查编排文件`docker-compose-clklog-XXX.yml`中`clklog-init`节点下的`INIT_MYSQL_*`的四个配置项，配置如果没有问题，先`drop`掉`mysql_tbl_*`相关表，然后重启`clklog-init`容器。

- **4. 前端页面没有数据**
  
    检查容器内各个服务是否正常。

  - 1）容器内服务有异常，则查看出现出现时的容器日志，根据异常日志调整相关配置后重启容器。

  - 2）容器内各服务正常，但前端页面没有数据。
    - 可能的原因：定时计算任务间隔时长设置不合理。
    - 解决方法：调整编排文件`docker-compose-clklog-XXX.yml`中`clklog-init`的`INIT_CALC_IN_ORDER_CRON`项的定时任务的间隔时长配置。
    > **前端数据说明**
    >
    >- 前端数据读取的是通过`clklog-init`服务定时计算生成的中间表的结果。
    >- 定时计算的间隔时长取决于`INIT_CALC_IN_ORDER_CRON`的配置。
    >- 间隔时长的设置需大于所有脚本执行总时长
    >   - 社区版
    >     - 总时长为开始执行`area_detail_bydate`到执行`visitor_detail_bysession`结束的时间差。
    >     - 如果数据库中没有看到`visitor_detail_bysession`的记录，先通过`INIT_CALC_IN_ORDER_CRON`把时间间隔调长。
    >   - 付费版
    >     - 总时长为开始执行`area_detail_bydate`到执行`user_detail_byinfo`结束的时间差。
    >     - 如果数据库中没有看到`user_detail_byinfo`的记录，先通过`INIT_CALC_IN_ORDER_CRON`把时间间隔调长。
    >- 脚本执行时间可通过 `docker logs <clklog-init容器ID> | grep <相应脚本名>`命令进行查看相应脚本执行日志。

- **5.【系统设置-日志汇总】功能没有数据**

  - 检查编排文件`docker-compose-clklog-XXX.yml`中`clklog-init`节点配置项`CLKLOG_MANAGE_LOG_STORE_PATH`是否配置正常，指向`clklog-receiver`的日志目录。

### clklog-manage

- **容器启动失败，反复重启**
  - 查看容器日志，查找重启原因，根据实际情况修改编排文件配置后，重启容器。
  - 没有明确报错信息的情况下可能是内存不足引起的，可尝试调大编排文件`docker-compose-clklog-XXX.yml`中`clklog-manage`节点的`memory`限制。

### clklog-api

- **容器启动失败，反复重启**
  - 查看容器日志，查找重启原因，根据实际情况修改编排文件配置后，重启容器。
  - 没有明确报错信息的情况下可能是内存不足引起的，可尝试调大编排文件`docker-compose-clklog-XXX.yml`中`clklog-api`节点的`memory`限制。

### mysql

- **宕机**
  - 查看容器日志，查找重启原因，根据实际情况修改编排文件配置后，重启容器。
  - 没有明确报错信息的情况下可能是内存不足引起的，可尝试调大编排文件`docker-compose-clklog-XXX-base.yml`中`mysql`节点的`memory`限制。

### clickhouse

- **宕机**
  - 查看容器日志，查找重启原因，根据实际情况修改编排文件配置后，重启容器。
  - 没有明确报错信息的情况下可能是内存不足引起的，可尝试调大编排文件`docker-compose-clklog-XXX-base.yml`中`clickhouse`节点`memory`限制。

### redis

- **宕机**
  - 查看容器日志，查找重启原因，根据实际情况修改编排文件配置后，重启容器。
  - 没有明确报错信息的情况下可能是内存不足引起的，可尝试调大编排文件`docker-compose-clklog-XXX-base.yml`中`redis`节点`memory`限制。

## 运维建议
  
  Docker部署虽然简单快速，但由于Docker容器在运行过程中可能会出现很多不可预测的问题，也需要根据实际运营情况调整编排文件中的相关配置，为了有效监控Docker容器运行状态，建议使用一些常用的监控工具如Prometheus、Grafana和cAdvisor等对Docker容器进行监控，这样运维人员可以有效地监控容器使用情况，并设置报警机制，以便及时响应潜在的问题，以提高系统的可靠性，也为系统的持续运行提供了保障。
