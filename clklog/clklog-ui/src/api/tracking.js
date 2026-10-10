import request from '@/utils/request'

export function trackTest(params) {
  return request({
    url: '/visitor/getVisitorProfile',
    method: 'post',
    data: params
  })
}
