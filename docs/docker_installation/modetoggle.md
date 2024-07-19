
## 快速模式切换标准模式

- 1. 停止并删除容器:`docker compose -f docker-compose-clklog-simple.yml down`。
- 2. 启动服务：`docker compose -f docker-compose-clklog-full.yml up -d`。
- 3. 后续步骤参考标准模式安装步骤。

## 标准模式切换快速模式

### 方式1

- 1. 将`docker-compose-clklog-full.yml`中设置`RECEIVER_ENABLE_SIMPLE_VERSION`为`true`。
- 2. 注释`docker-compose-clklog-full.yml`文件中`kafka`,`zookeeper`,`flink(jobmanager,taskmanager)`的相关配置,并停止对应容器。
- 3. 停止并删除`clklog-receiver`容器，再执行命令：`docker compose -f docker-compose-clklog-full.yml up -d`。

### 方式2

- 1. 停止并删除容器:`docker compose -f docker-compose-clklog-full.yml down`。
- 2. 启动服务： `docker compose -f docker-compose-clklog-simple.yml up -d`。
