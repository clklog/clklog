import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API ? process.env.VUE_APP_BASE_API : '/api'
// 获取地域
export function getAreaApi(params) {
  return request({
    url: BASE_API + '/area/getArea',
    method: 'post',
    data: params
  })
}
// 获取地域详情
export function getAreaDetailApi(params) {
  return request({
    url: BASE_API + '/area/getAreaDetail',
    method: 'post',
    data: params
  })
}
