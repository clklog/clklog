import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API
//获取访客
export function getVisitorApi(params) {
  return request({
    url: BASE_API + '/visitor/getVisitor',
    method: 'post',
    data: params
  })
}