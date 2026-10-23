# 版本信息

| **版权所有**     | ClkLog     |
| :--------------- | :--------- |
| **产品型号**     | 社区版   |
| **代码版本号**   | v1.4.0    |
| **版本发布时间** | 2026-10-23 |
| **升级前版本号：** |v1.3.1
 

# 升级说明

V1.4.0 的主要变化是**镜像版本号改为从 `.env` 读取**，另外前端 UI 有更新；后端代码与数据库结构均无变更。

> ⚠️ **必读**：升级时必须在你现有的 `.env` 中补一行 `CLKLOG_VER=v1.4.0`。
> 漏掉会导致镜像 tag 为空（如 `.../clklog/ui:`），启动报 `invalid reference format`。

# 升级内容

## 一、代码仓库模式调整

自2026-10-23日起，代码仓库模式由多个子仓库调整为单一主仓库管理，后续ClkLog维护方式切换至单一主仓库统一管理。

原多个子仓库管理代码的方式移除。原结构版本发布至V1.3.1截至，相关版本说明参考仓库：<https://github.com/clklog/clklog-deploy>

## 二、Docker 编排文件

两个发布包均为 18 个文件，目录结构一致、无增删。其中 15 个无实质内容变更（6 个完全一致，9 个仅换行符 CRLF→LF 差异），实质变更 3 个：

| 文件 | 变更 |
| --- | --- |
| `.env` | 新增 `CLKLOG_VER=v1.4.0` |
| `docker-compose-clklog-simple.yml` | 5 处业务镜像 `:1.3.1` → `:${CLKLOG_VER}`；gateway `nginx:1.29-alpine` → `nginx:1.23` |
| `docker-compose-clklog-full.yml` | 7 处业务镜像 `:1.3.1` → `:${CLKLOG_VER}` |

另有一处配置精简：`clklog-manage` 不再注入 `SPRING_SECURITY_USER_NAME` / `SPRING_SECURITY_USER_PASSWORD`，其健康检查随之去掉 `-u` 认证参数。该服务本身用 JWT 认证且 `/actuator/**` 已允许匿名访问，移除不影响功能；但 `clklog-api` 的健康检查仍在用这两项，**`.env` 中必须保留**。

## 三、应用变更

- **前端 UI**：登录页改版（含账号获取方式说明更新）；修复默认项目选中异常（缓存项目不在项目列表时回退首项）；退出登录改为只清理项目相关缓存；首页切换项目时增加 loading 提示。
- **后端与数据库**：5 个后端模块（api / init / manage / processing / receiver）无代码变更；`mysql_clklog.sql` 与 V1.3.1 完全一致，**无需执行任何建表语句**。

# 升级步骤

以快速模式为例，标准模式把文件名换成 `docker-compose-clklog-full.yml` 即可。以下命令默认在 `clklog-docker-compose` 目录内执行。

## 1. 备份

```bash
cd ..
tar -czf clklog-compose-1.3.1.bak.tar.gz --exclude='clklog_dc_data' clklog-docker-compose
cd clklog-docker-compose
```

## 2. 停止服务

```bash
docker compose -f docker-compose-clklog-simple.yml down
```

> 不要加 `-v`（会删除数据卷）。`clklog_dc_data` 目录全程不可删除。

## 3. 替换编排文件（保留 `.env`）

```bash
tar -zxvf clklog-docker-compose-v1.4.0.tar.gz -C /tmp
PKG=/tmp/clklog-docker-compose

cp -f  $PKG/docker-compose-clklog-*.yml .
cp -f  $PKG/clklog_init.sh .
cp -rf $PKG/clklog_dc_config/. ./clklog_dc_config/     # 注意：不要覆盖 .env
```

## 4. 补充版本号（关键）

```bash
grep -q '^CLKLOG_VER=' .env || sed -i '/^PROJECT_NAME=/a CLKLOG_VER=v1.4.0' .env
grep '^CLKLOG_VER=' .env          # 必须输出 CLKLOG_VER=v1.4.0
```

## 5. 拉取镜像并启动

```bash
docker compose -f docker-compose-clklog-simple.yml pull
docker compose -f docker-compose-clklog-simple.yml up -d
```

## 6. 验证

```bash
docker compose -f docker-compose-clklog-simple.yml config | grep 'image:'
```

业务镜像 tag 应全部为 `v1.4.0`；若出现 `.../api:`（tag 为空），说明第 4 步未生效：

```
image: registry.cn-shanghai.aliyuncs.com/clklog/api:v1.4.0
image: registry.cn-shanghai.aliyuncs.com/clklog/init:v1.4.0
image: registry.cn-shanghai.aliyuncs.com/clklog/manage:v1.4.0
image: registry.cn-shanghai.aliyuncs.com/clklog/receiver:v1.4.0
image: registry.cn-shanghai.aliyuncs.com/clklog/ui:v1.4.0
image: registry.cn-shanghai.aliyuncs.com/clklog/nginx:1.23
```

再确认容器均为 `Up`、前端可正常登录并切换项目、原有统计数据完整。

# 源码部署

后端 5 个模块自 V1.3.1 起无代码提交，无需替换 jar，只需重新部署 UI：
 
# 回滚

在 `clklog-docker-compose` 目录内执行（标准模式换文件名）：

```bash
docker compose -f docker-compose-clklog-simple.yml down

cd ..
rm -rf clklog-docker-compose
tar -xzf clklog-compose-1.3.1.bak.tar.gz

cd clklog-docker-compose
docker compose -f docker-compose-clklog-simple.yml up -d
```

`clklog_dc_data` 未包含在备份包中，全程保留，回滚后历史数据完整。

# 常见问题

**启动报 `invalid reference format`**：`.env` 缺少 `CLKLOG_VER`，见升级步骤第 4 步。

**`docker compose config` 只给 warning 不报错**：同样是缺 `CLKLOG_VER`。它不会失败，所以必须用第 6 步核对镜像 tag。

**能否直接用新包里的 `.env`**：不能。新包 `.env` 是默认示例值，直接覆盖会导致密码、`TOKEN_SECRET`、`PROCESSING_JOB_ID` 与现有环境不一致。只应在原 `.env` 上增加 `CLKLOG_VER`。

**是否需要重跑 `mysql_clklog.sql`**：不需要，两版一致。

**快速模式 nginx 从 `1.29-alpine` 降到 `1.23`**：网关配置 `gateway/simple/default.conf` 两版一致，功能不受影响，`pull` 拉取新镜像即可；若曾基于 `1.29-alpine` 自定义过镜像需相应调整。
