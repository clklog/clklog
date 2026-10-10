import request from '@/utils/request'

export function getDeviceDetailApi(params) {
  return request({
    url: '/device/getDeviceDetailList',
    method: 'post',
    data: params
  })
}