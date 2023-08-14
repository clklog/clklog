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

// 获取访客渠道分析
export function getVisitorChannelApi(params) {
  return request({
    url: '/visitor/getVisitorChannel',
    method: 'post',
    data: params
  })
}

// 1、左边用户基本信息
 export function getVisitorDetailinfoApi(params) {
  return request({
    url: '/visitor/getVisitorDetailinfo',
    method: 'post',
    data: params
  })
}
// 2、右边访问明细 ，按时间降序显示每次访问数据，接口为分页接口
 export function getVisitorSessionListApi(params) {
  return request({
    url: '/visitor/getVisitorSessionList',
    method: 'post',
    data: params
  })
}
// 3、每次访问明细数据，接口为分页接口
 export function getVisitorSessionUriListApi(params) {
  return request({
    url: '/visitor/getVisitorSessionUriList',
    method: 'post',
    data: params
  })
}