# clklog-ui

ClkLog 社区版前端项目。

## 环境要求

- Node.js `14.15.0`
- npm `>= 6`

建议使用 [nvm](https://github.com/nvm-sh/nvm) 切换 Node 版本：

```bash
nvm install 14.15.0
nvm use 14.15.0
```

## 功能

- 登录 / 注销
- 权限验证
  - 页面权限
  - 指令权限
  - 权限配置
  - 二步登录
- 多环境发布：dev / sit / stage / prod
- 全局功能
  - 动态侧边栏（支持多级路由嵌套）
  - 快捷导航（标签页）
  - Svg Sprite 图标
  - 自适应收缩侧边栏
- Excel：导出、前端可视化
- 错误页面
  - 401
  - 404
  - 423：接口返回 `code=423` 表示当前账号无该项目的数据访问权限，前端会跳转到无权限页面 
- 组件、综合实例、错误日志、引导页
- ECharts 图表

## 开发

```bash
# 克隆项目
git clone https://github.com/clklog/clklog-ui.git

# 进入项目目录
cd clklog-ui

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。
# 可通过指定镜像源解决 npm 下载速度慢的问题
npm install --registry=https://registry.npmmirror.com

# 启动服务
npm run dev
```

浏览器访问：`http://localhost:9527`

## 本地环境接口代理

修改 `vue.config.js` 中的代理配置：

- `/DEV-API` 的 `target`：clklog-api 接口地址，例如 `http://YOUR_DOMAIN/api`
- `/DEV-API-MANAGE` 的 `target`：clklog-manage 接口地址，例如 `http://YOUR_DOMAIN/manage`

## 生产环境接口配置

**方式一：发布前修改**

先改 `public/config.js`，再执行构建：

```js
BASE_API: "http://YOUR_DOMAIN/api"           // clklog-api 接口地址
BASE_API_MANAGE: "http://YOUR_DOMAIN/manage" // clklog-manage-api 接口地址
```

```bash
npm run build
```

**方式二：发布后修改**

先构建，再改 `dist/config.js` 中的同样配置项：

```bash
npm run build
```

```js
BASE_API: "http://YOUR_DOMAIN/api"           // clklog-api 接口地址
BASE_API_MANAGE: "http://YOUR_DOMAIN/manage" // clklog-manage-api 接口地址
```

## 其它命令

```bash
# 预览发布环境效果
npm run preview

# 预览发布环境效果 + 静态资源分析
npm run preview -- --report

# 代码格式检查
npm run lint

# 代码格式检查并自动修复
npm run lint -- --fix
```

## 在线 Demo

[https://demo.clklog.com/](https://demo.clklog.com/)
