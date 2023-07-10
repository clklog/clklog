import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API
// 获取Top10来源网站
export function getSourceWebsiteApi(params) {
  return request({
    url: BASE_API + '/sourcewebsite/getSourceWebsite',
    method: 'post',
    data: params
  })
}

// 获取来源网站详情
export function getSourceSiteDetailApi(params) {
  return request({
    url: BASE_API + '/sourcewebsite/getSourceSiteDetail',
    method: 'post',
    data: params
  })
}
