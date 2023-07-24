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
// 获取地域列表

export function getAreaDetailListApi(params) {
  return request({
    url: '/area/getAreaDetailList',
    method: 'post',
    data: params
  })
}
// 获取地域TOP10
export function getAreaDetailTop10Api(params) {
  return request({
    url: '/area/getAreaDetailTop10',
    method: 'post',
    data: params
  })
}