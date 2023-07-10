import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API
export function trackTest(params) {
  return request({
    url: BASE_API + '/visitor/getVisitorProfile',
    method: 'post',
    data: params
  })
}
