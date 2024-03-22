
## 环境准备
<!-- - Ubuntu SMP -->
- CentOS 7
- JDK 1.8
- Nginx 1.18
- Redis 3.2.4
- Zookeeper 3.7.1
- Kafka 2.12-3.3.1
- Flink 1.14.6
- Clickhouse 23.2.1
- nodejs >= 8.9
- npm >=3.0.0

## 初始化步骤参考

创建安装目录

```
mkdir /usr/local/services
```

### Java 1.8 安装参考

下载 jdk-8u211-linux-x64.rpm

```
rpm -ivh jdk-8u211-linux-x64.rpm
```

### nginx 安装参考

```
yum install nginx
systemctl start nginx
```

- ###### [nginx安装注意事项](question.md#nginx安装注意事项)

### Redis 3.2.4 安装参考

1. 从 Redis 官网下载安装包

    ```
    wget https://download.redis.io/releases/redis-3.2.4.tar.gz
    ```

2. 解压

    将安装包拷贝至`/usr/local/services/`目录并解压, 解压后进入`redis`安装目录：

    ```
    cd /usr/local/services/
    tar xzf redis-3.2.4.tar.gz
    cd redis-3.2.4
    ```

3. 编译

    ```
    make
    ```

4. 安装

    ```
    make install
    ```

    默认情况下，Redis 会被安装在`/usr/local/bin`目录下

- ###### [redis安装注意事项](question.md#redis安装注意事项)

5. 修改配置

    拷贝`redis.conf` 至`/etc/redis/redis.conf`

    ```
    mkdir /etc/redis
    cp redis.conf /etc/redis/redis.conf
    ```

    修改配置文件：`/etc/redis/redis.conf`

    ```
    vim /etc/redis/redis.conf

    protected-mode no
    daemonize yes
    appendonly yes
    ```  

6. 启动服务

    ```
    /usr/local/bin/redis-server /etc/redis/redis.conf
    ```

7. 检查安装是否成功

    ```
    cd /usr/local/bin
    ./redis-cli
    ```

<br>

### Zookeeper 3.7.1 安装参考

1. 从  Zookeeper  官网下载安装包

    ```
    wget --no-check-certificate https://dlcdn.apache.org/zookeeper/zookeeper-3.7.1/apache-zookeeper-3.7.1-bin.tar.gz
    ```

2. 解压

    将安装包拷贝至`/usr/local/services/`目录并解压, 解压后进入`Zookeeper`安装目录：

    ```
    cd /usr/local/services/
    tar -zxvf apache-zookeeper-3.7.1-bin.tar.gz
    cd apache-zookeeper-3.7.1-bin
    ```

3. 修改配置

    拷贝配置文件，并增加配置项

    ```
    cp ./conf/zoo_sample.cfg ./conf/zoo.cfg

    admin.serverPort=8887
    ```

4. 启动zk

    ```
    ./bin/zkServer.sh start
    ```

5. 检查

    ```
    ./bin/zkServer.sh status
    ```

    ![](../assets/imgs/zookeepercheck.png)

    未启动成功，从`./logs`里查看启动日志

<br>

### Kafka 2.12-3.3.1 安装参考

1. 从 Kafka 官网下载安装包

    ```
    wget --no-check-certificate https://archive.apache.org/dist/kafka/3.3.1/kafka_2.12-3.3.1.tgz
    ```

2. 解压

    将安装包拷贝至`/usr/local/services/`目录并解压, 解压后进入`Kafka`安装目录：

    ```
    cd /usr/local/services/
    tar -xzf kafka_2.12-3.3.1.tgz
    cd kafka_2.12-3.3.1
    ```

3. 启动

    ```
    ./bin/kafka-server-start.sh  -daemon config/server.properties
    ```

4. 检查

    创建名为`test`的`topic`

    ```
    ./bin/kafka-topics.sh --create --bootstrap-server 127.0.0.1:9092 --replication-factor 1 --partitions 1 --topic test
    ```

    ![](../assets/imgs/createtop.png)

    查看`topic`

    ```
    ./bin/kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --describe --topic test
    ```

    ![](../assets/imgs/checktopic.png)

    不报错正常，如果未启动成功，从`./logs`里查看启动日志

### Flink 1.14.6 安装参考

1. 从 Flink官网下载安装包

    ```
    wget https://archive.apache.org/dist/flink/flink-1.14.6/flink-1.14.6-bin-scala_2.12.tgz
    ```

2. 解压

    将安装包拷贝至`/usr/local/services/`目录，重命名为`flink-1.14.6.tgz`后解压, 解压后进入`Flink`安装目录：

    ```
    cd /usr/local/services/
    mv flink-1.14.6-bin-scala_2.12.tgz flink-1.14.6.tgz
    tar -xzf flink-1.14.6.tgz
    cd flink-1.14.6
    ```

3. 启动

    建议不要使用root启动 , 启动代码参考如下:

    ```
    ./bin/start-cluster.sh
    ```

4. 检查

    <http://localhost:8081/>

    ![](../assets/imgs/preview.png)

### Clickhouse 23.2.1 安装参考

1. 安装clickhouse

    ```
    yum install -y yum-utils
    rpm --import https://repo.yandex.ru/clickhouse/CLICKHOUSE-KEY.GPG
    yum-config-manager --add-repo https://repo.yandex.ru/clickhouse/rpm/stable/x86_64
    yum install clickhouse-server clickhouse-client
    ```

2. 修改用户验证信息

    修改`/etc/clickhouse-server/users.xml`文件，在<users>标签下设置用户验证信息。比如，我们设定一个用户名为`default`，密码为`123456`。

    更多设置请参考ClickHouse官方文档。

3. 启动数据库

    ```
    sudo systemctl enable clickhouse-server  
    sudo systemctl start clickhouse-server  
    sudo systemctl status clickhouse-server  
    ```

4. 登录数据库

    ```
    clickhouse-client -u default --password 123456
    ```
