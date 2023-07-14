import request from '@/utils/request'

//获取访客
export function getVisitorApi(params) {
  return request({
    url: '/visitor/getVisitor',
    method: 'post',
    data: params
  })
}
// 获取访客详情
export function getVisitorDetailApi(params) {
  return request({
    url: '/visitor/getVisitorDetail',
    method: 'post',
    data: params
  })
}
