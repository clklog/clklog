// /channel/getChannelDetail
import request from '@/utils/request'

// 获取Top10受访页面
export function getChannelDetailApi(params) {
  return request({
    url: '/channel/getChannelDetail',
    method: 'post',
    data: params
  })
}