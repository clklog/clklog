import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API
// 获取Top10受访页面
export function getVisitUriApi(params) {
  return request({
    url: BASE_API + '/visituri/getVisitUri',
    method: 'post',
    data: params
  })
}
// 获取地域详情
export function getVisitUriDetailApi(params) {
  return request({
    url: BASE_API + '/visituri/getVisitUriDetail',
    method: 'post',
    data: params
  })
}
