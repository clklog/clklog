import request from '@/utils/request'

// 获取Top10来源网站
export function getSourceWebsiteApi(params) {
  return request({
    url: '/sourcewebsite/getSourceWebsite',
    method: 'post',
    data: params
  })
}

// 获取来源网站详情
export function getSourceSiteDetailApi(params) {
  return request({
    url: '/sourcewebsite/getSourceSiteDetail',
    method: 'post',
    data: params
  })
}
