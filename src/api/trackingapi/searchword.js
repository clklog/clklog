import request from '@/utils/request'

const BASE_API = process.env.VUE_APP_BASE_API ? process.env.VUE_APP_BASE_API : '/api'
// 获取Top10搜索词
export function getSearchWordApi(params) {
  return request({
    url: BASE_API + '/searchword/getSearchWord',
    method: 'post',
    data: params
  })
}

// 获取搜索词详情
export function getSearchWordDetailApi(params) {
  return request({
    url: BASE_API + '/searchword/getSearchWordDetail',
    method: 'post',
    data: params
  })
}
