import request from '@/utils/request'

export function getDeviceDetailApi(params) {
  return request({
    url: '/device/getDeviceDetail',
    method: 'post',
    data: params
  })
}