import request from '@/utils/request'

export function getSearchWordApi(params) {
  return request({
    url: '/searchword/getSearchWordTop10',
    method: 'post',
    data: params
  })
}

// 获取搜索词详情
export function getSearchWordDetailApi(params) {
  return request({
    url: '/searchword/getSearchWordDetail',
    method: 'post',
    data: params
  })
}
