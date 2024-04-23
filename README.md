## 功能

```
- 登录 / 注销

- 权限验证
  - 页面权限
  - 指令权限
  - 权限配置

  - 二步登录

- 多环境发布
  - dev
  - sit
  - stage
  - prod

- 全局功能
  - 动态侧边栏（支持多级路由嵌套）
  - 快捷导航(标签页)
  - Svg Sprite 图标
  - 自适应收缩侧边栏

- Excel
  - 导出excel
  - 前端可视化excel


- 错误页面
  - 401
  - 404

- 組件
- 综合实例
- 错误日志
- 引导页
- ECharts 图表
```

## 开发

```bash
# 克隆项目
git clone https://github.com/clklog/clklog-ui.git

# 进入项目目录
cd clklog-ui

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 启动服务
npm run dev
```
## 本地环境跨域服务代理

```bash
1、vue.config.js 文件中 /DEV-API 配置 
   target='代理地址'

```
## 发布

```bash
# 构建生产环境

方式一、public/config.js 文件中配置 
       BASE_API: "api接口基础路径"
       npm run build

方式二、npm run build 
       dist/config.js
       BASE_API: "api接口基础路径"
```

## 其它

```bash
# 预览发布环境效果
npm run preview

# 预览发布环境效果 + 静态资源分析
npm run preview -- --report

# 代码格式检查
npm run lint

# 代码格式检查并自动修复
npm run lint -- --fix

## Online Demo

[在线 Demo](https://demo.clklog.com/)

## Donate
```
