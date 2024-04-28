import request from '@/utils/request'
// 头部
export function getSourceWebSiteTotalApi(params) {
  return request({
    url: '/sourcewebsite/getSourceWebSiteTotal',
    method: 'post',
    data: params
  })
}
// 获取Top10来源网站10 
export function getSourceWebsiteApi(params) {
  return request({
    // url: '/sourcewebsite/getSourceWebsite',
    url: '/sourcewebsite/getSourceWebSiteTop10',
    method: 'post',
    data: params
  })
}

// 分页获取来源网站详情 详情 
export function getSourceSiteDetailApi(params) {
  return request({
    url: '/sourcewebsite/getSourceWebSiteDetail',
    method: 'post',
    data: params
  })
}

// 获取Top10来源网站详情
export function getSourceWebSiteDetailTop10Api(params) {
  return request({
    url: '/sourcewebsite/getSourceWebSiteDetailTop10',
    method: 'post',
    data: params
  })
}
