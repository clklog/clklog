import request from '@/utils/request'

export function totalSummaryApi(params) {
  return request({
    url: '/appCrashed/totalSummary',
    method: 'post',
    data: params
  })
}
// 分页获取崩溃数据
export function getPageApi(params) {
  return request({
    url: '/appCrashed/getPage',
    method: 'post',
    data: params
  })
}
// 获取崩溃分组数据
export function groupedSummaryApi(params) {
    return request({
      url: '/appCrashed/groupedSummary',
      method: 'post',
      data: params
    })
  }
// 获取崩溃分组数据
export function trendSummaryApi(params) {
    return request({
      url: '/appCrashed/trendSummary',
      method: 'post',
      data: params
    })
  }
// 获取崩溃分组数据
export function getPagedSummaryApi(params) {
    return request({
      url: '/appCrashed/getPagedSummary',
      method: 'post',
      data: params
    })
  }