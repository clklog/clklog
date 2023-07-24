import request from '@/utils/request'

// 获取Top10来源网站
export function getSourceWebsiteApi(params) {
  return request({
    url: '/sourcewebsite/getSourceWebsite',
    method: 'post',
    data: params
  })
}

// 分页获取来源网站详情
export function getSourceSiteDetailApi(params) {
  return request({
    url: 'sourcewebsite/getSourceWebSiteDetail',
    method: 'post',
    data: params
  })
}
// 获取Top10来源网站详情
export function getSourceWebSiteDetailTop10Api(params) {
  return request({
    url: 'sourcewebsite/getSourceWebSiteDetailTop10',
    method: 'post',
    data: params
  })
}