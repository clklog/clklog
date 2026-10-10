import request from '@/utils/request'

// 获取Top10受访页面
export function getVisitUriApi(params) {
  return request({
    url: '/visituri/getVisitUri',
    method: 'post',
    data: params
  })
}

export function getVisitUriDetailApi(params) {
  return request({
    url: '/visituri/getVisitUriDetailList',
    method: 'post',
    data: params
  })
}

export function getVisitUriTotalApi(params) {
  return request({
    url: '/visituri/getVisitUriTotal',
    method: 'post',
    data: params
  })
}
// 树状结构
export function getVisitUriPathTreeTotalApi(params) {
  return request({
    url: '/visituri/getVisitUriPathTreeTotal',
    method: 'post',
    data: params
  })
}
// 资源路径top10页面统计数据
export function getVisitUriListOfUriPathApi(params) {
  return request({
    url: '/visituri/getVisitUriListOfUriPath',
    method: 'post',
    data: params
  })
}