import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API
export function getFlowApi(params) {
  return request({
    url: BASE_API + '/flow/getFlow',
    method: 'post',
    data: params
  })
}

export function getFlowTrendApi(params) {
  return request({
    url: BASE_API + '/flow/getFlowTrend',
    method: 'post',
    data: params
  })
}

export function getFlowTrendDetailApi(params) {
  return request({
    url: BASE_API + '/flow/getFlowTrendDetail',
    method: 'post',
    data: params
  })
}
