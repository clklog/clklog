import request from '@/utils/request'

//获取访客
export function getVisitorApi(params) {
  return request({
    url: '/visitor/getVisitor',
    method: 'post',
    data: params
  })
}
//获取访客统计
export function getVisitorTotalApi(params) {
  return request({
    url: '/visitor/getVisitorTotal',
    method: 'post',
    data: params
  })
}
// 获取访客详情列表
export function getVisitorListApi(params) {
  return request({
    url: '/visitor/getVisitorList',
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
