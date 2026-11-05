# ClkLog 部署模板（compose-template）

## UA 实时分流升级

升级 receiver 或 processing 前，先在目标 ClickHouse 库执行
`clklog/clklog-init/scripts/upgrade_ua_routing_v1.sql`，替换 `${CLKLOG_LOG_DB}` 为目标库名。
脚本不迁移历史数据；新安装使用更新后的 `scripts/init.sql`。

本版本默认将正常事件写入 `log_analysis`，机器人事件写入 `log_analysis_bot`。
现有 `excludedUa` 命中后写机器人表，不再丢弃，也不进入常规统计。
构建 receiver 或 processing 前先执行 `mvn -f clklog/clklog-traffic-rules/pom.xml clean install`。

搜索引擎 IP 库为 Redis Hash `ClkLog:Traffic:SearchEngineIpLibrary`，field 为
`engine|cidr`，value 为 JSON，包含 `engine`、`cidr`、`source`、`verified_at`、
`expires_at`、`library_version`。时间为毫秒时间戳，单 IP 使用 `/32` 或 `/128`。
只写入来源可信且有效期明确的记录；空库或未命中表示未验证，不表示伪造。
本阶段不提供 DNS 验证或库自动维护。

接收队列使用内存，进程退出可能丢失未落库事件。receiver 批次处理失败时记录错误并等待
5 秒后继续处理，不重试失败批次；无论数据异常还是 ClickHouse、Kafka 临时故障，失败批次
都可能丢失。Flink 重放可能产生重复，不保证端到端 exactly-once。
回滚可恢复旧服务版本并保留机器人表。

> ⚠️ 这是**源码仓内的部署模板目录**，**不能直接用于部署**。
> 推荐直接下载 Release 中的 **`clklog-docker-compose-<版本号>.tar.gz`**（已替好版本号、开箱即用）。

## 模板说明

本目录与发布包的区别：

| 来源 | `.env` 中的 `CLKLOG_VER` | 是否可直接 `docker compose up` |
| --- | --- | --- |
| 本目录（模板） | `VER_PLACEHOLDER`（占位符） | ❌ 否，需先替换 |
| Release 包 `clklog-docker-compose-<tag>.tar.gz` | 已替换为真实版本号 | ✅ 是 |

## 若需在本目录本地部署（不下载 Release 包）

1. **替换版本号**：编辑 `.env`，把 `CLKLOG_VER=VER_PLACEHOLDER` 改为目标版本（如 `v1.4.1`）。
2. **创建数据目录**：执行 `bash clklog_init.sh`（仅创建 redis / kafka / zookeeper / flink 检查点所需的本地数据目录并赋权）。
3. **启动**：
   ```bash
   docker compose -f docker-compose-clklog-simple.yml up -d      # 快速模式
   # 或
   docker compose -f docker-compose-clklog-full.yml up -d        # 标准模式
   ```

## 文件说明

- `docker-compose-clklog-simple.yml` / `*-base.yml`：快速模式部署（仅含基础服务，不含含kafka、Flink）。
- `docker-compose-clklog-full.yml` / `*-base.yml`：标准模式部署（含kafka、Flink等全部服务）。
- `.env`：集中配置（数据库账号密码、版本号等），由 compose 文件引用。
- `clklog_init.sh`：首次部署前创建本地数据卷目录。
- `clklog_dc_config/`：各服务配置文件。

