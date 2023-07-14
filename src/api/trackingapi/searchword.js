import request from '@/utils/request'

// 获取Top10搜索词
export function getSearchWordApi(params) {
  return request({
    url: '/searchword/getSearchWord',
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
