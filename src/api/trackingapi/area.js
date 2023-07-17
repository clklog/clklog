import request from '@/utils/request'

// 获取地域
export function getAreaApi(params) {
  return request({
    url: '/area/getArea',
    method: 'post',
    data: params
  })
}
// 获取地域详情
export function getAreaDetailApi(params) {
  return request({
    url: '/area/getAreaDetail',
    method: 'post',
    data: params
  })
}
