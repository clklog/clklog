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
## 本地环境接口服务代理配置修改
```
 # 修改vue.config.js 文件中的代理配置 
'/DEV-API'的'target'为clklog-api接口的代理地址 'http://YOUR_DOMAIN/api'
'/DEV-API-MANAGE'的'target'为clklog-manage接口的代理地址 'http://YOUR_DOMAIN/api/manage'
```


## 生产环境接口服务代理配置修改
```
 # 方式一、先修改public/config.js 文件中配置，再发布
    BASE_API: "http://YOUR_DOMAIN/api" //clklog-api的接口的代理地址
    BASE_API_MANAGE: "http://YOUR_DOMAIN/manage" //clklog-manage-api接口的代理地址
    npm run build

 # 方式二、发布后修改代理配置
    npm run build 
    dist/config.js
    BASE_API: "http://YOUR_DOMAIN/api" //clklog-api的接口的代理地址
    BASE_API_MANAGE: "http://YOUR_DOMAIN/manage" //clklog-manage-api接口的代理地址

```

## 其它

## 预览发布环境效果
npm run preview

## 预览发布环境效果 + 静态资源分析
npm run preview -- --report

## 代码格式检查
npm run lint

## 代码格式检查并自动修复
npm run lint -- --fix

## Online Demo

[在线 Demo](https://demo.clklog.com/)

## Donate
```
