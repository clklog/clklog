import request from '@/utils/request'

export function getFlowApi(params) {
  return request({
    url: '/flow/getFlow',
    method: 'post',
    data: params
  })
}
// 头部
export function getFlowTotalApi(params) {
  return request({
    url: '/flow/getFlowTotal',
    method: 'post',
    data: params
  })
}
// echarts
export function getFlowTrendApi(params) {
  return request({
    url: '/flow/getFlowTrend',
    method: 'post',
    data: params
  })
}
// table detail
export function getFlowDetailApi(params) {
  return request({
    url: '/flow/getFlowDetail',
    method: 'post',
    data: params
  })
}
// 弃用 进行拆分接口
export function getFlowTrendDetailApi(params) {
  return request({
    url: '/flow/getFlowTrendDetail',
    method: 'post',
    data: params
  })
}
