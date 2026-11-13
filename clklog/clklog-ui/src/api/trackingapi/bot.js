import request from '@/utils/request'

// 机器人流量概览（机器人/真人同口径对比）
export function getBotOverviewApi(params) {
  return request({
    url: '/bot/getBotOverview',
    method: 'post',
    data: params
  })
}

// 机器人分类汇总（环图：各分类请求IP数及IP占比）
export function getBotCategoryApi(params) {
  return request({
    url: '/bot/getBotCategory',
    method: 'post',
    data: params
  })
}

// 机器人地域分布
export function getBotAreaApi(params) {
  return request({
    url: '/bot/getBotArea',
    method: 'post',
    data: params
  })
}

// 搜索引擎抓取汇总（抓取页面数 vs IP数）
export function getBotEngineSummaryApi(params) {
  return request({
    url: '/bot/getBotEngineSummary',
    method: 'post',
    data: params
  })
}

// 分页获取引擎抓取页面明细
export function getBotEnginePagesApi(params) {
  return request({
    url: '/bot/getBotEnginePages',
    method: 'post',
    data: params
  })
}

// 分页获取机器人IP明细
export function getBotIpDetailApi(params) {
  return request({
    url: '/bot/getBotIpDetail',
    method: 'post',
    data: params
  })
}
