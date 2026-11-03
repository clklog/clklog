# ClkLog 部署模板（compose-template）

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

