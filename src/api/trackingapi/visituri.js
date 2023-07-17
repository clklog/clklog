import request from '@/utils/request'

// 获取Top10受访页面
export function getVisitUriApi(params) {
  return request({
    url: '/visituri/getVisitUri',
    method: 'post',
    data: params
  })
}
// 获取地域详情
export function getVisitUriDetailApi(params) {
  return request({
    url: '/visituri/getVisitUriDetail',
    method: 'post',
    data: params
  })
}
