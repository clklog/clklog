# UA 流量识别规则文档

> 依据实现：`src/main/java/com/zcunsoft/clklog/traffic/UaTrafficClassifier.java`
> 结果载体：`src/main/java/com/zcunsoft/clklog/traffic/TrafficDecision.java`

## 1. 概述

社区版实时 **UA 单事件识别器**，接收端与 Flink 两条入库链路共用同一套规则。

- 纯 UA 直判：不依赖 PV、会话数量或离线行为评分。
- 多条规则可同时命中，各命中原因分别记录在 `reasons` 列表。
- 只要任一规则命中即判为机器人，`botScore=100`；全部未命中则 `botScore=0`。

## 2. 输入与输出

### 输入

| 参数 | 说明 |
| --- | --- |
| `ua` | 请求或 SDK 提供的原始 User-Agent |
| `ip` | 事件来源 IP（用于搜索引擎/AI 来源的 IP 段校验） |
| `deviceClass` | Yauaa 解析的设备类型（`DEVICE_CLASS`） |
| `agentClass` | Yauaa 解析的客户端类型（`AGENT_CLASS`） |
| `agentName` | Yauaa 解析的客户端名称（`AGENT_NAME`，仅用于无头浏览器识别） |
| `rules` | 项目当前可用的 UA 黑名单（`UaRuleSet`） |
| `snapshot` | 已加载到内存的搜索引擎/AI 来源 IP 库（`SearchEngineIpSnapshot`） |

### 输出（TrafficDecision）

| 字段 | 说明 |
| --- | --- |
| `reasons` | 命中原因列表，可含多条：`ua_blacklist` / `ua_device_class` / `ua_agent_class` / `ua_headless` / `ua_attack_payload` / `ua_ai_crawler` / `search_engine_ua` / `ua_script` |
| `botScore` | 命中任意规则为 100，否则 0 |
| `botCategory` | 机器人五分类：`ai_crawler` / `search_engine` / `headless_browser` / `script_crawler` / `other`；普通流量为空 |
| `botSubcategory` | 分类下的子类（见第 7 节） |
| `searchEngine` | 第一个匹配的搜索引擎标识 |
| `verificationStatus` | 搜索引擎 IP 校验状态（未声明搜索引擎时为 `not_applicable`） |
| `aiVerificationStatus` | AI 来源 IP 校验状态（未声明 AI 来源时为 `not_applicable`） |

> IP 校验状态仅作附加信息：未验证或与其他引擎匹配**不会**撤销 UA 直判，也不会据此认定 UA 伪造。

## 3. 直判规则（并行记录原因）

以下规则对每条事件全部执行，命中即向 `reasons` 追加对应原因：

| 原因 | 判定条件 |
| --- | --- |
| `ua_blacklist` | UA 命中项目配置的 UA 黑名单（`UaRuleSet.matches`） |
| `ua_device_class` | `deviceClass` ∈ {`robot`, `robot mobile`, `robot imitator`, `hacker`}（忽略大小写） |
| `ua_agent_class` | `agentClass` ∈ {`robot`, `robot mobile`}（忽略大小写） |
| `ua_headless` | UA 或 `agentName` 满足任一无头特征（见 3.1） |
| `ua_attack_payload` | UA 含攻击载荷特征（见 3.2） |
| `ua_ai_crawler` | UA 命中第一个 AI 来源规则（见第 5 节），**命中后不再匹配搜索引擎** |
| `search_engine_ua` | 无 AI 来源命中时，UA 命中第一个搜索引擎规则（见第 4 节） |
| `ua_script` | 无 AI/搜索引擎命中时，UA 命中脚本规则（见第 6 节） |

### 3.1 无头浏览器特征（`ua_headless`）

满足任一即命中：

- UA 含 `headlesschrome`（词边界）或 `chrome headless`（忽略大小写）
- UA 含通用标识 `headless`（词边界，忽略大小写）
- UA 含 `lightpanda`
- UA 含 `phantomjs`
- `agentName`（Yauaa AGENT_NAME）含 `headlesschrome`

### 3.2 攻击载荷特征（`ua_attack_payload`）

UA（忽略大小写）满足任一即命中：

- 含 `${jndi:` —— Log4Shell 探测
- 含 `() { :; }` —— Shellshock 载荷
- 以 `cve-<数字` 开头 —— CVE 漏洞探针

## 4. 搜索引擎规则

11 个引擎，标识与正则按下标对应；按数组顺序匹配，**只取第一个命中的引擎**。全部忽略大小写。

| 引擎标识 | 匹配正则 | 说明 |
| --- | --- | --- |
| `baidu` | `baiduspider` | 覆盖 Baiduspider 及 -image/-video/-news/-favo/-cpro/-ads/-render 8 个官方变体（均以 Baiduspider 开头） |
| `google` | `googlebot\|google-inspectiontool\|storebot-google\|googleother\|adsbot-google\|apis-google` | |
| `bing` | `bingbot\|bingvideopreview\|bingpreview\|msnbot\|microsoftpreview\|adidxbot` | |
| `360` | `haosouspider\|360spider` | |
| `sogou` | `sogou[ _-]*((web\|inst\|news\|pic\|orion\|test)[ _-]*)?spider` | 允许 `sogou web spider` 等分隔写法 |
| `yandex` | `yandexbot\|yandeximages\|yandexvideo\|yandexnews\|yandexadditionalbot` | |
| `yisou` | `yisouspider` | 神马 |
| `duckduckgo` | `duckduckbot\|duckduckgo-favicons-bot` | |
| `apple` | `applebot` | 注意：`applebot-extended` 先被 AI 规则截获（见第 5 节） |
| `huawei` | `petalbot` | |
| `brave` | `bravebot` | |

官方文档出处（见源码类注释）：百度、Google、Bing、360、搜狗、Yandex、DuckDuckGo、Apple、PetalBot、Brave 各自的爬虫说明页。

## 5. AI 来源规则

15 个来源，标识与正则按下标对应；按数组顺序匹配，**只取第一个命中的来源**。标识同时作为 IP 库中厂商网段的查询键。

**AI 来源先于搜索引擎匹配**，两类声明互斥：命中 AI 来源后不再匹配搜索引擎。这保证了包含关系场景（如 `applebot-extended` 包含 `applebot`）优先归入 AI。

| 来源标识 | 匹配正则 |
| --- | --- |
| `meta` | `meta-external(agent\|fetcher)\|facebookbot` |
| `bytedance` | `bytespider\|tiktokspider` |
| `reflection-ai` | `reflectionbot` |
| `anthropic` | `claude(bot\|-user\|-searchbot\|-web)` |
| `openai` | `gptbot\|oai-searchbot\|chatgpt-user` |
| `duckassist` | `duckassistbot` |
| `perplexity` | `perplexity(bot\|-user)` |
| `deepseek` | `deepseekbot\|deepseek-harness` |
| `youchat` | `youbot` |
| `commoncrawl` | `ccbot` |
| `amazon` | `amazonbot` |
| `cohere` | `cohere-ai\|coherebot` |
| `iask` | `iaskspider` |
| `mistral` | `mistralai-(user\|training)` |
| `applebot-extended` | `applebot-extended` |

## 6. 脚本/程序化访问规则（`ua_script`）

仅当 **AI 来源与搜索引擎均未命中** 时执行（保持分类优先级）。UA 转小写后**包含**关键词即命中该类别；类别按 `LinkedHashMap` 声明顺序比较，跨类别关键词同时命中时按声明顺序归类，键名作为脚本子类标识。

| 优先级 | 类别键 | 关键词 |
| --- | --- | --- |
| 1 | `SECURITY_SCANNER` | sqlmap, nmap, zgrab, zmap, shodan, censys, zoomeye, fofa, xpanse, binaryedge, masscan, nuclei |
| 2 | `PYTHON` | python-requests, urllib, httpx, aiohttp, scrapy, mechanize |
| 3 | `COMMAND_LINE` | curl, wget, aria2, libcurl, httpie |
| 4 | `JAVA` | apache-httpclient, okhttp, jsoup, retrofit |
| 5 | `GO` | go-http-client, fasthttp, go-resty |
| 6 | `NODEJS` | axios, undici, node-fetch |
| 7 | `RUBY` | typhoeus, faraday |
| 8 | `DART` | dart/, dart:io |
| 9 | `SEO_TOOL` | ahrefs, semrush, mj12bot, dotbot, dataforseo, barkrowler, babbar |
| 10 | `SOCIAL_PREVIEW` | twitterbot, discordbot, telegrambot, whatsapp, facebookexternalhit, slackbot, linkedinbot |
| 11 | `MONITOR_ARCHIVER` | uptimerobot, pingdom, site24x7, statuscake, archive.org_bot, ia_archiver |
| 12 | `PHP` | php/, guzzle, wordpress, jetpack |

兜底规则：所有具体关键词类别未命中后，再匹配通用机器人词 `GENERIC_BOT`——带词边界的 `*bot` / `spider` / `crawl*` / `scrap*`（忽略大小写）。兜底置于最后，避免 `UptimeRobot` 等被误归为通用机器人。

特殊返回值：UA 为 null 或空串返回 `EMPTY_UA`（不算脚本命中）；无任何命中返回 `UNKNOWN`（不算脚本命中）。

## 7. 机器人五分类与子类

分类按以下优先级取**首个命中**；普通流量两类均为空。

| 优先级 | botCategory | botSubcategory |
| --- | --- | --- |
| 1 | `ai_crawler` | 命中的 AI 来源键（第 5 节） |
| 2 | `search_engine` | 命中的引擎标识（第 4 节） |
| 3 | `headless_browser` | 无头子类（见 7.1） |
| 4 | `script_crawler` | 命中的脚本类别键（第 6 节） |
| 5 | `other` | 其他子类（见 7.2） |

### 7.1 无头浏览器子类

- `headless_chrome`：UA 含 `headlesschrome` / `chrome headless`，或 `agentName` 含 `headlesschrome`
- `lightpanda`：UA 含 `lightpanda`
- `phantomjs`：UA 含 `phantomjs`
- `other`：其余（如命中通用 `headless` 标识）

### 7.2 other 子类

按具体程度依次判断，取首个命中：

- `attack_payload`：命中攻击载荷特征（最具体）
- `hacker`：`deviceClass = hacker`
- `robot_imitator`：`deviceClass = robot imitator`
- `robot_unknown`：`agentClass` 或 `deviceClass` 为 `robot` / `robot mobile`（未分类机器人）
- `blacklist`：仅 UA 黑名单命中

## 8. IP 校验（互斥）

- 未声明 AI 来源：只做**搜索引擎** IP 校验（`snapshot.match(engine, ip)`），AI 校验状态为 `not_applicable`。
- 声明了 AI 来源：只做 **AI 来源** IP 校验（以来源键查询厂商网段），搜索引擎校验状态为 `not_applicable`。
- 校验结果状态仅随结果落库作参考，不改变分流判定。

## 9. 判定流程（文字版流程图）

```
输入 ua / ip / deviceClass / agentClass / agentName / 黑名单 / IP库快照
  │
  ├─ ① 并行直判（全部执行，原因可叠加）
  │     黑名单 ────────────→ ua_blacklist
  │     deviceClass 4 类 ──→ ua_device_class
  │     agentClass 2 类 ───→ ua_agent_class
  │     无头特征 ──────────→ ua_headless
  │     攻击载荷 ──────────→ ua_attack_payload
  │
  ├─ ② AI 来源正则（按声明顺序，取首个命中）──→ ua_ai_crawler
  │     └─ 未命中 → 搜索引擎正则（取首个命中）──→ search_engine_ua
  │           └─ 仍未命中 → 脚本规则（按类别顺序 + 通用兜底）──→ ua_script
  │
  ├─ ③ IP 校验（互斥：有 AI 来源查 AI 网段，否则查搜索引擎网段）
  │
  └─ ④ 五分类取首个命中：ai_crawler > search_engine > headless_browser
        > script_crawler > other；全部为空即普通流量（botScore=0）
```

## 10. 维护注意事项

1. `ENGINES` / `ENGINE_PATTERNS`、`AI_SOURCES` / `AI_SOURCE_PATTERNS` 各自按下标一一对应，新增或调整引擎/来源时需同时维护两个数组。
2. `SCRIPT_UA_RULES` 使用 `LinkedHashMap` 固定优先级（`Map.of` 迭代顺序不确定），新增脚本类别时注意声明位置即优先级。
3. 调整 AI/搜索引擎/脚本的匹配顺序会改变分类结果，需同步更新相关测试（`UaTrafficClassifierTest`）。
