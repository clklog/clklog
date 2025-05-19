import request from '@/utils/request'
// tracking api 
// 分页获取事件分析数据
export function getEventListApi(params) {
  return request({
    url: '/event/getEventList',
    method: 'post',
    data: params
  })
}
// 分页获取日志事件明细数据
export function getLogEventlListApi(params) {
  return request({
    // url: '/event/getLogEventlList',
    url: '/event/getLogEventList',
    method: 'post',
    data: params
  })
}
// 分页获取事件详情分析数据
export function getEventDetailListApi(params) {
  return request({
    url: '/event/getEventDetailList',
    method: 'post',
    data: params
  })
}

// 获取事件属性清单
export function getEventPropertyListApi(params) {
  return request({
    url: '/event/getEventPropertyList',
    method: 'post',
    data: params
  })
}

// 分页获取事件详情分析数据
export function getEventDetailTrendApi(params) {
  return request({
    url: '/event/getEventDetailTrend',
    method: 'post',
    data: params
  })
}
