import request from '@/utils/request'

export function getFlowApi(params) {
  return request({
    url: '/flow/getFlow',
    method: 'post',
    data: params
  })
}

export function getFlowTrendApi(params) {
  return request({
    url: '/flow/getFlowTrend',
    method: 'post',
    data: params
  })
}

export function getFlowTrendDetailApi(params) {
  return request({
    url: '/flow/getFlowTrendDetail',
    method: 'post',
    data: params
  })
}
